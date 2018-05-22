package com.galaxtone.noneuclideanportals.network;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import com.galaxtone.noneuclideanportals.Main;
import com.galaxtone.noneuclideanportals.Portal;
import com.galaxtone.noneuclideanportals.Portal.Reference;
import com.galaxtone.noneuclideanportals.WorldData;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import scala.actors.threadpool.Arrays;

public class PacketPortal extends ValidatedPacket implements IMessage {

	private enum Type {
		CREATE, DESTROY, SYNC
	}

	private static class Wrapper<T> {
		public final Type type;
		public final T value;
		
		public Wrapper(Type type, T value) {
			this.type = type;
			this.value = value;
		}
	}

	private Wrapper<?> wrapper;

	private PacketPortal(Wrapper<?> wrapper) {
		this.wrapper = wrapper;
	}

	public PacketPortal() {}

	public static PacketPortal create(@Nonnull Portal portal) {
		return new PacketPortal(new Wrapper<Portal>(Type.CREATE, portal));
		
	}

	public static PacketPortal destroy(int index) {
		return new PacketPortal(new Wrapper<Integer>(Type.DESTROY, index));
	}

	public static PacketPortal sync(@Nonnull Portal[] portals) {
		return new PacketPortal(new Wrapper<Portal[]>(Type.SYNC, portals));
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		int capacity = buffer.capacity();
		if (capacity == 0) {
			this.setInvalid("Invalid portal packet: Empty buffer");
			return;
		}
		
		int typeByte = buffer.readByte() & 0xFF;
		if (typeByte > 3) {
			this.setInvalid("Invalid portal packet: Type byte out of range. (%d) [Range: 0 - 3]", typeByte);
			return;
		}
		
		Type type = Type.values()[typeByte];
		switch(type) {
			case CREATE:
				if (capacity != 22) {
					this.setInvalid("Invalid CREATE portal packet: Invalid buffer length (%d) [Expected: 22]", capacity);
					return;
				}
				
				try {
					this.wrapper = new Wrapper<Portal>(type, PacketPortal.readPortal(buffer));
				} catch(Exception exception) {
					this.setInvalid("Invalid CREATE portal packet: %s", exception.getMessage());
					return;
				}
				break;
			case DESTROY:
				if (capacity != 3) {
					this.setInvalid("Invalid DESTROY portal packet: Invalid buffer length (%d) [Expected: 3]", capacity);
					return;
				}
				
				this.wrapper = new Wrapper<Integer>(type, new Integer(buffer.readShort() & 0xFFFF));
				break;
			case SYNC:
				if (capacity < 3) {
					this.setInvalid("Invalid SYNC portal packet: Invalid buffer length (%d) [Expected: >3]", capacity);
					return;
				}
				
				int lengthByte = buffer.readShort() & 0xFFFF;
				int wantedCapacity = lengthByte * 21 + 3;
				if (capacity != lengthByte * 21 + 3) {
					this.setInvalid("Invalid SYNC portal packet: Invalid buffer length (%d) [Exepcted: %d]", capacity, wantedCapacity);
					return;
				}
				
				Portal[] portals = new Portal[lengthByte];
				
				buffer.writeShort(portals.length);
				for (int i = 0; i < lengthByte; i++) {
					try {
						portals[i] = PacketPortal.readPortal(buffer);
					} catch(Exception exception) {
						this.setInvalid("Invalid SYNC portal packet (Portal #%d): %s", i, exception.getMessage());
						return;
					}
				}
				
				this.wrapper = new Wrapper<Portal[]>(type, portals);
				break;
		}
		
		this.setValid();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeByte(this.wrapper.type.ordinal());
		
		switch(this.wrapper.type) {
			case CREATE:
				PacketPortal.writePortal(buffer, (Portal) this.wrapper.value);
				break;
			case DESTROY:
				buffer.writeShort((int) this.wrapper.value & 0xFFFF);
				break;
			case SYNC:
				Portal[] portals = (Portal[]) this.wrapper.value;
				
				buffer.writeShort(portals.length);
				for (Portal portal : portals) PacketPortal.writePortal(buffer, portal);
				break;
		}
		
		this.setValid();
	}

	public static Portal readPortal(ByteBuf buffer) throws Exception {
		int x = buffer.readInt();
		int y = buffer.readInt();
		int z = buffer.readInt();
		
		int axis = buffer.readByte() & 0xFF;
		if (axis > 2) throw new Exception(String.format("Axis byte out of range. (%d) [Range: 0 - 2]", axis));
		
		int width = buffer.readByte() & 0xFF;
		if (width > 15) throw new Exception(String.format("Width byte out of range. (%d) [Range: 0 - 15]", width));
		
		int height = buffer.readByte() & 0xFF;
		if (height > 15) throw new Exception(String.format("Height byte out of range. (%d) [Range: 0 - 15]", height));
		
		int frontDirection = buffer.readByte() & 0xFF;
		if (frontDirection > 1) throw new Exception(String.format("Front direction byte out of range. (%d) [Range: 0 - 1]", frontDirection));
		
		int backDirection = buffer.readByte() & 0xFF;
		if (backDirection > 1) throw new Exception(String.format("Back direction byte out of range. (%d) [Range: 0 - 1]", backDirection));
		
		int frontId = buffer.readShort() & 0xFFFF;
		int backId = buffer.readShort() & 0xFFFF;
		
		Portal portal = Portal.from(new BlockPos(x, y, z), Axis.values()[axis], width + 1, height + 1);
		if (frontId > 0) Portal.Reference.create(portal.front, AxisDirection.values()[frontDirection], frontId - 1);
		if (backId > 0) Portal.Reference.create(portal.back, AxisDirection.values()[backDirection], backId - 1);
		
		return portal;
	}

	public static void writePortal(ByteBuf buffer, Portal portal) {
		buffer.writeInt(portal.pos.getX());
		buffer.writeInt(portal.pos.getY());
		buffer.writeInt(portal.pos.getZ());
		
		buffer.writeByte(portal.axis.ordinal());
		buffer.writeByte(portal.width);
		buffer.writeByte(portal.height);
		
		Reference frontReference = portal.front.reference;
		if (frontReference == null) {
			buffer.writeByte(0);
			buffer.writeShort(0);
		} else {
			buffer.writeByte(frontReference.direction.ordinal());
			buffer.writeShort(frontReference.index + 1);
		}
		
		Reference backReference = portal.back.reference;
		if (backReference == null) {
			buffer.writeByte(0);
			buffer.writeShort(0);
		} else {
			buffer.writeByte(backReference.direction.ordinal());
			buffer.writeShort(backReference.index + 1);
		}
	}

	public static final class Handler implements IMessageHandler<PacketPortal, IMessage> {

		@SuppressWarnings("unchecked")
		@Override
		public IMessage onMessage(PacketPortal packet, MessageContext context) {
			if (!packet.isValid()) {
				Main.logger.warn(packet.getReason());
				return null;
			}
			
			if (context.side == Side.SERVER && !context.getServerHandler().playerEntity.canCommandSenderUseCommand(2, null)) {
				Main.logger.warn("Cannot accept portal packet from un-opped player.");
				return null;
			}
			
			if (packet.wrapper.type == Type.SYNC) {
				if (context.side == Side.SERVER) {
					Main.logger.warn("Cannot accept snyc portal packets from players.");
					return null;
				}
				
				Portal.list = new ArrayList<Portal>(Arrays.asList((Portal[]) packet.wrapper.value));
			} else {
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						if (packet.wrapper.type == Type.CREATE) {
							Portal portal = (Portal) packet.wrapper.value;
							
							if (context.side == Side.SERVER) {
								World world = context.getServerHandler().playerEntity.getEntityWorld();
								if (!world.isBlockLoaded(portal.pos)) {
									Main.logger.warn("Cannot accept portal packets refering to an unloaded location.");
									return;
								}
								
								WorldData.get(world).markDirty();
							}
							
							portal.index = Portal.list.size();
							Portal.list.add(portal);
						} else {
							int index = (Integer) packet.wrapper.value;
							if (index >= Portal.list.size()) {
								Main.logger.warn("Destroy portal packet refers to non-existing portal");
								return;
							}
							
							Portal portal = Portal.list.get(index);
							
							if (context.side == Side.SERVER) {
								World world = context.getServerHandler().playerEntity.getEntityWorld();
								if (!world.isBlockLoaded(portal.pos)) {
									Main.logger.warn("Cannot accept destroy packets refering to an unloaded location.");
									return;
								}
								
								WorldData.get(world).markDirty();
							}
							
							portal.back.destination.destination = null;
							portal.back.destination = null;
							
							portal.front.destination.destination = null;
							portal.front.destination = null;
							
							Portal.list.remove(index);
						}
						
						if (context.side == Side.SERVER) {
							Main.network.sendToAll(packet);
						}
					}
				};
				
				if (context.side == Side.CLIENT) Minecraft.getMinecraft().addScheduledTask(runnable);
				else context.getServerHandler().playerEntity.getServer().addScheduledTask(runnable);
			}
			
			return null;
		}
	}
}
