package at.tyron.vintagecraft.World;

import java.util.ArrayList;
import java.util.Hashtable;

import at.tyron.vintagecraft.VintageCraft;
import at.tyron.vintagecraft.Interfaces.IMechanicalPowerDevice;
import at.tyron.vintagecraft.Interfaces.IMechanicalPowerNetworkNode;
import at.tyron.vintagecraft.Interfaces.IMechanicalPowerNetworkRelay;
import at.tyron.vintagecraft.Network.MechanicalNetworkNBTPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class MechanicalNetwork {
	MechnicalNetworkManager myManager;

	ArrayList<IMechanicalPowerNetworkNode> powerNodes = new ArrayList<IMechanicalPowerNetworkNode>();
	ArrayList<IMechanicalPowerNetworkRelay> powerRelays = new ArrayList<IMechanicalPowerNetworkRelay>();
	
	// A network may only contain devices of same torque and speed.
	// Power converters create a new network
	float totalAvailableTorque;
	float totalResistance;
	float speed;
	
	public int networkId;
	
	int direction;
	EnumFacing directionFromFacing; // From which facing is the direction?
	
	BlockPos firstPowerNode;
	
	private float angle = 0;
	
	public boolean isDead;
	
	// Client field
	public float serverSideAngle;
	
	
	public void updateAngle(float speed) {	
		angle -= speed / 10f;
		angle = angle % 360f;
	
		serverSideAngle -= speed / 10f;
		serverSideAngle = serverSideAngle % 360f;
	}
	
	public float getAngle() {
		return angle;
	}

	
	MechanicalNetwork(MechnicalNetworkManager myManager, int networkId) {
		this.networkId = networkId;
		this.myManager = myManager;
		
		speed = 0;
		totalAvailableTorque = 0;
		totalResistance = 0;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public float getAvailableTorque() {
		return totalAvailableTorque;
	}

	public float getTotalResistance() {
		return totalResistance;
	}
	
	public boolean isClockWise(EnumFacing facing) {
		if (facing == directionFromFacing) {
			return direction > 0;
		}
		return direction < 0;
	}

	public float getRemainingResistance() {
		return Math.max(0, totalResistance - totalAvailableTorque);
	}
	public float getUnusedTorque() {
		return Math.max(0, totalAvailableTorque - totalResistance);
	}

	public void register(IMechanicalPowerDevice device) {
		if (device instanceof IMechanicalPowerNetworkNode && !powerNodes.contains(device)) {
			powerNodes.add((IMechanicalPowerNetworkNode)device);
		}
		if (device instanceof IMechanicalPowerNetworkRelay && !powerRelays.contains(device)) {
			powerRelays.add((IMechanicalPowerNetworkRelay)device);	
		}
	}
	
	public void unregister(IMechanicalPowerDevice device) {
		if (device instanceof IMechanicalPowerNetworkNode) {
			powerNodes.remove((IMechanicalPowerNetworkNode)device);
		}
		if (device instanceof IMechanicalPowerNetworkRelay) {
			powerRelays.remove((IMechanicalPowerNetworkRelay)device);	
		}
		
		rebuildNetwork();
	}

	
	
	// Tick Events are called by the Network Managers
	
	public void clientTick(ClientTickEvent event) {
		if (speed < 0.001) return;
		
		updateAngle(speed);
		
		// Each tick, add 5% of server<->client angle difference
		float diff = 0.01f * (serverSideAngle - angle);
		if (diff > 0.005f) {
			//System.out.println(diff);
			angle -= diff;
		}
	}
	
	public void serverTick(World world) {
		updateAngle(speed);
	
		if (world.getWorldTime() % 5 == 0) {
			updateNetwork();
		}
		
		if (world.getWorldTime() % 40 == 0) {
			sentNetworkToClients();
		}
	}
	
	
	public void sentNetworkToClients() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		//System.out.println("send " + speed + " / " + networkId);
		/*VintageCraft.packetPipeline.sendToDimension(
			 
			world.provider.getDimensionId()
		);*/
		//System.out.println("sent! " + speed + " / " + networkId);
		
		
		
		MechanicalNetworkNBTPacket packet = new MechanicalNetworkNBTPacket(nbt, networkId);
		for (Object plr : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			EntityPlayerMP player = (EntityPlayerMP)plr;

			if (player.dimension == myManager.getWorld().provider.getDimensionId()) {
				VintageCraft.packetPipeline.sendTo(packet, player);
			}
	    }		
	}
	
	// Should run every 5 ticks
	public void updateNetwork() {
		
		/* 1. Verify network */
		IMechanicalPowerNetworkNode[] powernodesArray = powerNodes.toArray(new IMechanicalPowerNetworkNode[0]);
		for (IMechanicalPowerNetworkNode node : powernodesArray) {
			if (!node.exists()) {
				
				unregister(node);
			}
		}
		if (powerNodes.size() == 0) {
			//System.out.println("no more power nodes");
			isDead = true;
			return;
		}
		
		
		/* 2. Determine total available torque and total resistance of the network */
		totalAvailableTorque = 0;
		totalResistance = 0;
		
		IMechanicalPowerNetworkNode dominantNode = null; 
		
		
		for (IMechanicalPowerNetworkNode powerNode : powerNodes) {
			totalAvailableTorque += powerNode.getTorque(this);
			totalResistance += powerNode.getResistance(this);
			
			if (dominantNode == null || powerNode.getTorque(this) > dominantNode.getTorque(this)) {
				dominantNode = powerNode;
			}
		}
		directionFromFacing = dominantNode.getDirectionFromFacing();
		
		
		
		/* 3. Unconsumed torque changes the network speed */
		
		// Positive free torque => increase speed until maxSpeed
		// Negative free torque => decrease speed until -maxSpeed
		// No free torque => lower speed until 0
		
		float unusedTorque = Math.abs(totalAvailableTorque) - totalResistance;
		int speedChange = (totalAvailableTorque > 0) ? 1 : -1;
		if (unusedTorque <= 0) {
			speedChange = 0;
		}
		
		/*if (networkId == 6) {
			System.out.println("unusedTorque: " + unusedTorque + " / speedChange: " + speedChange);
		}*/
		
		float step = 0.75f;
		
		switch (speedChange) {
			case 1:
				speed = speed + step;
				break;
			
			case -1:
				speed = speed - step;
				break;
			
			case 0:
				if (speed > 0) {
					speed = Math.max(0, speed - step);
				}
				if (speed < 0) {
					speed = Math.min(0, speed + step);
				}
				break;
		}
		
		
		//if (networkId == 1)
		//System.out.println(networkId + ": " + unusedTorque);
		
		/* 4. Set direction, also did the direction change? Propagate it through the network */
		
		int olddirection = direction;
		direction = (int)Math.signum(speed);
		
		if (olddirection != direction) {
			//System.out.println("=====propagate direction to neighbours");
			for (IMechanicalPowerNetworkNode powerNode : powerNodes) {
				// FIXME: This assumes there is only 1 power producer per network
				if (powerNode.getTorque(this) > 0) {
					//System.out.println("sent!");
					powerNode.propagateDirectionToNeightbours(
						myManager.getUniquePropagationId(), 
						powerNode.getOutputSideForNetworkPropagation(), 
						direction > 0
					);
					break;
				}
			}
		}

	}

	
	
	
	public void rediscoverNetwork(World world) {
	//	System.out.println("rediscovering networks");
		TileEntity te = world.getTileEntity(firstPowerNode);
		if (te instanceof IMechanicalPowerNetworkNode) {
		//	System.out.println("go");
			IMechanicalPowerNetworkNode node = (IMechanicalPowerNetworkNode)te;
			node.propagateNetworkToNeighbours(myManager.getUniquePropagationId(), networkId, node.getOutputSideForNetworkPropagation());
		}
		
		//System.out.println("rediscovery complete, found " + powerNodes.size() + " power nodes");
	}

	
	public void rebuildNetwork() {
		//System.out.println("rebuilding network");
		
		for (IMechanicalPowerDevice device : powerRelays) {
			if (device != null) {
				device.clearNetwork();
			}
		}
		
		if (powerNodes.size() == 0) {
			//System.out.println("no more power nodes in the network :(");
			return;
		}
		IMechanicalPowerNetworkNode firstNode = powerNodes.get(0);
		Hashtable<IMechanicalPowerNetworkNode, EnumFacing> otherNodes = new Hashtable<IMechanicalPowerNetworkNode, EnumFacing>();
		
		
		for (IMechanicalPowerNetworkNode powernode : powerNodes) {
			if (powernode != null && firstNode != powernode) {
				otherNodes.put(powernode, powernode.getFacing(this));
				powernode.clearNetwork();
			}
		}
		
		if (firstNode == null) {
			//System.out.println("node is null");
			return;
		}
		
		powerNodes.clear();
		powerRelays.clear();
		
		firstNode.propagateNetworkToNeighbours(
			myManager.getUniquePropagationId(), 
			networkId, 
			firstNode.getOutputSideForNetworkPropagation()
		);
		
		
		// Whatever power nodes are now not in the current network now need to have the current network forked for them
		for (IMechanicalPowerNetworkNode otherNode : otherNodes.keySet()) {
			boolean found = false;
			for (MechanicalNetwork network : otherNode.getNetworks()) {
				if (network.networkId == networkId) {
					found = true;
				}
			}
			if (!found) {
				//System.out.println("forked network");
				forkMechanicalNetwork(otherNode, otherNodes.get(otherNode));
			}
		}
		
		//System.out.println("total networks in game: " + myManager.networksById.size());
	}
	
	
	public void forkMechanicalNetwork(IMechanicalPowerNetworkNode powernode, EnumFacing facing) {
		powernode.createMechanicalNetwork(this, facing);
		
	}

	public boolean isClockWise() {
		return direction > 0;
	}
	
	public int getDirection() {
		return direction;
	}
	
	
	public void readFromNBT(NBTTagCompound nbt, boolean lagHidingAngleUpdate) {
		networkId = nbt.getInteger("networkId");
		totalAvailableTorque = nbt.getFloat("totalAvailableTorque");
		totalResistance = nbt.getFloat("totalResistance");
		speed = nbt.getFloat("speed");
		direction = nbt.getInteger("direction");
		
		if (lagHidingAngleUpdate) {
			serverSideAngle = nbt.getFloat("angle");
//			angle = nbt.getFloat("angle");
		} else {
			angle = nbt.getFloat("angle");
		}
		
		firstPowerNode = new BlockPos(
			nbt.getInteger("firstNodeX"),
			nbt.getInteger("firstNodeY"),
			nbt.getInteger("firstNodeZ")
		);
	}
	
	
	public void readFromNBTForked(NBTTagCompound nbt) {
		totalAvailableTorque = nbt.getFloat("totalAvailableTorque");
		totalResistance = nbt.getFloat("totalResistance");
		speed = nbt.getFloat("speed");
		direction = nbt.getInteger("direction");
		angle = nbt.getFloat("angle");
	}
	
	
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("networkId", networkId);
		nbt.setFloat("totalAvailableTorque", totalAvailableTorque);
		nbt.setFloat("totalResistance", totalResistance);
		nbt.setFloat("speed", speed);
		nbt.setInteger("direction", direction);
		nbt.setFloat("angle", angle);
		
		nbt.setInteger("firstNodeX", firstPowerNode.getX());
		nbt.setInteger("firstNodeY", firstPowerNode.getY());
		nbt.setInteger("firstNodeZ", firstPowerNode.getZ());
	}
	
	
	
}
