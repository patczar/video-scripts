package net.patrykczarnik.vp;

class VPScriptEntryFile extends VPScriptEntry {
	private final String path;
	private Double start;
	private Double end;
	
	private VPScriptEntryFile(String path, Double start, Double end) {
		this.path = path;
		this.start = start;
		this.end = end;
	}
	
	public static VPScriptEntryFile of(String path, Double start, Double end) {
		return new VPScriptEntryFile(path, start, end);
	}
	
	public static VPScriptEntryFile ofPath(String path) {
		return new VPScriptEntryFile(path, null, null);
	}

	public String getPath() {
		return path;
	}
	
	public Double getStart() {
		return start;
	}

	public void setStart(Double start) {
		this.start = start;
	}

	public Double getEnd() {
		return end;
	}

	public void setEnd(Double end) {
		this.end = end;
	}

	@Override
	public String toString() {
		return "file " + path + " -s " + start + " -e " + end;
	}

	
	
}
