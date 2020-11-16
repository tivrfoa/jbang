package dev.jbang;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ScriptCache {
	private Path jarPath;
	private Path nativeImagePath;
	private Path runCommandPath;

	public Path getJarPath() {
		return jarPath;
	}

	public void setJarPath(Path jarPath) {
		this.jarPath = jarPath;
	}

	public Path getNativeImagePath() {
		return nativeImagePath;
	}

	public void setNativeImagePath(Path nativeImagePath) {
		this.nativeImagePath = nativeImagePath;
	}

	public Path getRunCommandPath() {
		return runCommandPath;
	}

	public void setRunCommandPath(Path runCommandPath) {
		this.runCommandPath = runCommandPath;
	}

	public static ScriptCache getScriptCache(String entrypoint) throws IOException {
		ScriptCache scriptCache = new ScriptCache();
		File entryFile;
		if (Util.isURL(entrypoint)) {
			Path path = Util.getFirstFileInURLCache(entrypoint);
			if (path == null)
				return scriptCache;
			entryFile = path.toFile();
		} else {
			entryFile = new File(entrypoint);
		}
		File baseDir = Settings.getCacheDir(Settings.CacheClass.jars).toFile();
		File tmpJarDir = new File(baseDir, entryFile.getName() +
				"." + Util.getStableID(entryFile));

		File outjar = new File(tmpJarDir.getParentFile(), tmpJarDir.getName() + ".jar");
		if (outjar.exists()) {
			scriptCache.setJarPath(outjar.toPath());
			File runCmd = new File(outjar.toString() + ".run");
			if (runCmd.exists())
				scriptCache.setRunCommandPath(runCmd.toPath());
		}
		File nativeImageFile;
		if (Util.isWindows()) {
			nativeImageFile = new File(outjar.toString() + ".exe");
		} else {
			nativeImageFile = new File(outjar.toString() + ".bin");
		}
		if (nativeImageFile.exists())
			scriptCache.setNativeImagePath(nativeImageFile.toPath());

		return scriptCache;
	}

}
