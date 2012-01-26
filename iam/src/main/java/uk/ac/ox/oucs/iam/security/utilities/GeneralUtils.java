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
	/**
	 * Copy a file
	 * 
	 * @param src
	 *            the source file to be copied
	 * @param dst
	 *            the destination file
	 * @throws IOException
	 */
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

	
	/**
	 * Read the contents of a file and return as a generic object that can be
	 * cast to something more useful
	 * @param fileName the file to read
	 * @return a generic object representing the file contents
	 * @throws IOException
	 */
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

	
	/**
	 * Create a file with specific contents (such as a key) 
	 * @param fileName the file to create
	 * @param o the contents for the file
	 */
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
