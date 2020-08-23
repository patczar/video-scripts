#!/usr/bin/python3

listfilename = "frames.csv"
outfilename = "times.txt"

class Frame:
	def __init__(self, row):
		self.n = int(row[0])
		self.pts = int(row[1])
		self.time = float(row[2])
		self.pos = int(row[3])
		self.type = row[4]
		self.iskey = (row[5].strip() == "1")

	def __str__(self):
		return "frame no %d, pts: %d, time: %f" % (self.n, self.pts , self.time)

def readFrameList(fileName):
	frames = []
	with open(fileName, "r") as f:
		for line in f:
			fields = line.split(";");
			frame = Frame(fields)
			frames.append(frame)
	return frames

def findIFrameAfter(frames, time):
	for frame in frames:
		if(frame.time >= time and frame.iskey):
			return frame

def commandForArgs(frames, start_time, end_time, speed, chunk):
	start_frame = findIFrameAfter(frames, start_time)
	end_frame = findIFrameAfter(frames, end_time)
	#ffmpeg -y -ss $start -t $t -i "$src" -f image2 -i "$logo" -filter_complex "setpts=PTS/$speed,$filtry,`speed $speed`$vf" $codec chunk${nr}.mp4
	#sox audio.flac chunk${nr}.flac trim $start $t speed $speed vol 3

print("Czytam dane")
frames = readFrameList(listfilename)
print("Dane wczytane")

for t in [454, 548, 581, 970, 1115, 1380, 1420, 1452, 1850]:
	print("Szukam dla", t)
	frame = findIFrameAfter(frames, t)
	print(frame)


#	with open(outfilename, "w") as w:
