package uk.ac.ox.oucs.iam.security.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class GeneralUtils {
	public static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public static Object readObjectFromFile(String fileName) throws IOException {
		Object o = null;

		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(fileName);
			in = new ObjectInputStream(fis);
			o = in.readObject();
			in.close();
			fis.close();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		return o;
	}

	public static void writeObject(String fileName, Object o) {
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {

			fos = new FileOutputStream(fileName);
			out = new ObjectOutputStream(fos);
			out.writeObject(o);
			out.close();
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
