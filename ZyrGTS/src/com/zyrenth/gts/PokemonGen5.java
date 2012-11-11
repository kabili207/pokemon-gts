package com.zyrenth.gts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PokemonGen5 extends Pokemon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2918646173091332187L;

	public static final String shiftind = "\u0000\u0001\u0002\u0003\u0000" + "\u0001\u0003\u0002\u0000\u0002\u0001\u0003\u0000\u0002\u0003\u0001"
			+ "\u0000\u0003\u0001\u0002\u0000\u0003\u0002\u0001\u0001\u0000\u0002" + "\u0003\u0001\u0000\u0003\u0002\u0001\u0002\u0000\u0003\u0001\u0002"
			+ "\u0003\u0000\u0001\u0003\u0000\u0002\u0001\u0003\u0002\u0000\u0002" + "\u0000\u0001\u0003\u0002\u0000\u0003\u0001\u0002\u0001\u0000\u0003"
			+ "\u0002\u0001\u0003\u0000\u0002\u0003\u0000\u0001\u0002\u0003\u0001" + "\u0000\u0003\u0000\u0001\u0002\u0003\u0000\u0002\u0001\u0003\u0001"
			+ "\u0000\u0002\u0003\u0001\u0002\u0000\u0003\u0002\u0000\u0001\u0003" + "\u0002\u0001\u0000";

	private byte[] data;

	public PokemonGen5() {

	}

	public PokemonGen5(byte[] data) {
		if (data.length == 220) {
			this.data = data;
		} else if (data.length == 136) {
			// TODO: Add party data

			try {

				ByteArrayOutputStream bais = new ByteArrayOutputStream();

				bais.write(data);
				// TODO: write __level function
				// lv = __level()
				// id = pkm[0x08] + (pkm[0x09] << 8)

				bais.write(new byte[] { 0, 0, 0, 0 }); // 0x88 to 0x8b
				// s += chr(lv) # 0x8c
				bais.write(0); // 0x8d
				// TODO: write __stats function
				// s += __stats(lv, id) # 0x8e to 0x9b
				bais.write(new byte[] { 0, 0, 0, 0, 0 }); // 0x9c to 0xa0
				bais.write(2); // 0xa1
				bais.write(7); // 0xa2
				bais.write(new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
						(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
						(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff }); // 0xa3 to 0xb9
				bais.write(new byte[] { 0, 0 }); // 0xba to 0xbb
				bais.write(new byte[] { (byte) 0xff, (byte) 0xff }); // 0xbc to 0xbd
				if ((data[0x40] & 4) == 4) // 0xbe to 0xbf; de01 if genderless, 0000 otherwise
					bais.write(new byte[] { (byte) 0xde, (byte) 0x01 });
				else
					bais.write(new byte[] { 0, 0 });
				bais.write(new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff }); // 0xc0 to 0xc6
				bais.write((byte) 0x88); // 0xc7
				bais.write(1); // 0xc8
				bais.write(new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff }); // 0xc9 to 0xcd
				bais.write((byte) 0xac); // 0xce
				bais.write(1); // 0xcf
				bais.write(new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff }); // 0xd0 to 0xd3
				bais.write(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }); // 0xd4 to 0xdb

				this.data = bais.toByteArray();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}

		} else {
			// TODO: Throw some kind of exception
		}
	}

	@Override
	public String getOTName() {
		byte[] b = Arrays.copyOfRange(data, 0x68, 0x78);
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(b));

		String retValue = "";

		for (int i = 0; i < b.length / 2; ++i) {
			try {
				char t = Character.reverseBytes(in.readChar());
				if (t == '\uffff')
					break;
				retValue += t;
			} catch (Exception e) {
			}
		}
		return retValue;
	}

	@Override
	public String getNickname() {
		byte[] b = Arrays.copyOfRange(data, 0x48, 0x5e);
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(b));

		String retValue = "";

		for (int i = 0; i < b.length / 2; ++i) {
			try {
				char t = Character.reverseBytes(in.readChar());
				if (t == '\uffff')
					break;
				retValue += t;
			} catch (IOException e) {
			}
		}
		return retValue;
	}

	@Override
	public short getNatID() {
		byte[] b = Arrays.copyOfRange(data, 0x08, 0x0a);
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(b));

		short id = 0;
		try {
			id = Short.reverseBytes(in.readShort());
		} catch (Exception e) {
		}

		return id;
	}

	@Override
	public short getOTID() {
		byte[] b = Arrays.copyOfRange(data, 0x0c, 0x0e);
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(b));

		short id = 0;
		try {
			id = Short.reverseBytes(in.readShort());
		} catch (Exception e) {
		}

		return id;
	}

	@Override
	public short getOTSecretID() {
		byte[] b = Arrays.copyOfRange(data, 0x0e, 0x10);
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(b));

		short id = 0;
		try {
			id = Short.reverseBytes(in.readShort());
		} catch (Exception e) {
		}

		return id;
	}

	@Override
	public int getPID() {
		byte[] b = Arrays.copyOfRange(data, 0x00, 0x04);
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(b));

		int id = 0;
		try {
			id = Integer.reverseBytes(in.readInt());
		} catch (Exception e) {
		}

		return id;
	}

	@Override
	public boolean isShiny() {
		long pid = getPID() & 0xFFFFFFFFL;
		System.out.println(pid);
		long tmp = ((getOTID() ^ getOTSecretID()) ^ ((pid >> 16) ^ (pid & 0xffff))) & 0xFFFFFFFFL;
		System.out.println(tmp);
		return tmp < 8;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.write(data.length);
		out.write(data);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		int length = in.readInt();
		data = new byte[length];
		in.readFully(data);
	}

	public byte[] encode() throws IOException {
		return encode(data);
	}

	public static byte[] makePkm(byte[] bytes) throws IOException {
		byte[] ar = Arrays.copyOfRange(bytes, 12, 232);
		return decode(ar);
	}

	public static byte[] decode(byte[] bytes) throws IOException {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
		List<Character> shifted = new ArrayList<Character>();

		int v = Integer.reverseBytes(in.readInt());

		shifted.add(Character.reverseBytes(in.readChar()));
		shifted.add(Character.reverseBytes(in.readChar()));

		for (int i = 0; i < bytes.length / 2 - 4; ++i) {
			shifted.add(Character.reverseBytes(in.readChar()));
		}

		Rnd rand = new Rnd(shifted.get(1));

		for (int i = 2; i < 66; ++i) {
			shifted.set(i, (char) (shifted.get(i) ^ rand.make()));
		}

		if (shifted.size() > 66) {
			rand = new Rnd((int) v);
			for (int i = 66; i < shifted.size(); ++i) {
				shifted.set(i, (char) (shifted.get(i) ^ rand.make()));
			}
		}

		int shift = ((((int) v) >> 0xD & 0x1F) % 24);

		ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);
		DataOutputStream os = new DataOutputStream(bos);

		List<Short> ord = new ArrayList<Short>();

		for (char i : shiftind.substring(shift * 4, shift * 4 + 4).toCharArray()) {
			ord.add((short) i);
		}

		os.writeInt(Integer.reverseBytes((int) v));
		char ts = shifted.get(0).charValue();
		os.writeChar(Character.reverseBytes(ts));

		ts = shifted.get(1).charValue();
		os.writeChar(Character.reverseBytes(ts));

		for (short i = 0; i < 4; ++i) {
			int sOrd = (3 + 16 * ord.indexOf(i)) - 1;
			int tOrd = (19 + 16 * ord.indexOf(i)) - 1;

			for (char j : shifted.subList(sOrd, tOrd)) {
				ts = j;
				os.writeChar(Character.reverseBytes(ts));
			}
		}

		for (int i = 66; i < shifted.size(); ++i) {
			ts = shifted.get(i).charValue();
			os.writeChar(Character.reverseBytes(ts));
		}
		return bos.toByteArray();

	}

	public static byte[] encode(byte[] bytes) throws IOException {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
		List<Character> shifted = new ArrayList<Character>();

		int v = Integer.reverseBytes(in.readInt());

		shifted.add(Character.reverseBytes(in.readChar()));
		shifted.add(Character.reverseBytes(in.readChar()));

		for (int i = 0; i < bytes.length / 2 - 4; ++i) {
			shifted.add(Character.reverseBytes(in.readChar()));
		}

		int shift = ((((int) v) >> 0xD & 0x1F) % 24);

		List<Short> ord = new ArrayList<Short>();

		for (char i : shiftind.substring(shift * 4, shift * 4 + 4).toCharArray()) {
			ord.add((short) i);
		}

		char ts;
		List<Character> temp = new ArrayList<Character>();

		temp.add(shifted.get(0));
		temp.add(shifted.get(1));

		for (short i : ord) {
			int sOrd = (3 + 16 * i) - 1;
			int tOrd = (19 + 16 * i) - 1;

			for (char j : shifted.subList(sOrd, tOrd)) {
				temp.add(j);
				// ts = j;
				// os.writeChar(Character.reverseBytes(ts));
			}
		}

		for (int i = 66; i < shifted.size(); ++i) {
			ts = shifted.get(i).charValue();
			temp.add(ts);
			// os.writeChar(Character.reverseBytes(ts));
		}

		Rnd rand = new Rnd(temp.get(1));

		for (int i = 2; i < 66; ++i) {
			temp.set(i, (char) (temp.get(i) ^ rand.make()));
		}

		if (shifted.size() > 66) {
			rand = new Rnd((int) v);
			for (int i = 66; i < temp.size(); ++i) {
				temp.set(i, (char) (temp.get(i) ^ rand.make()));
			}
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);
		DataOutputStream os = new DataOutputStream(bos);

		os.writeInt(Integer.reverseBytes((int) v));

		for (int i = 0; i < temp.size(); ++i) {
			ts = temp.get(i).charValue();
			os.writeChar(Character.reverseBytes(ts));
		}

		return bos.toByteArray();

	}

	private static class Rnd {
		private int rngseed;

		public Rnd(int rngseed) {
			this.rngseed = rngseed;
		}

		public int make() {
			rngseed = 0x41C64E6D * rngseed + 0x6073;
			rngseed &= 0xFFFFFFFF;
			return (rngseed >> 16);
		}

	}

	public byte[] getData() {
		return data;
	}
}
