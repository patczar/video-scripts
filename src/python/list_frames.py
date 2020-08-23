#!/usr/bin/python3

from subprocess import call
import re
import sys

video_file = sys.argv[1]
filename = video_file + ".frames.txt"
vlistfilename = video_file + ".vframes.csv"
alistfilename = video_file + ".aframes.csv"

print("Odpalam ffmpeg")
ffout = open(filename, "wb")
call(["ffmpeg", "-y", "-i",video_file,"-filter_complex","[v:0]showinfo;[a:0]ashowinfo","-f","null","/dev/null"], stderr=ffout)
#call(["ffmpeg", "-y", "-i",video_file,"-filter_complex","[v:0]showinfo","-f","null","/dev/null"], stderr=ffout)
ffout.close()
print("Zakończone ffmpeg")

pv = re.compile(r"\[Parsed_showinfo.*\]\s+n:\s*(\d+)\s+pts:\s*(\d+)\s+pts_time:\s*(\d+(?:\.\d+)?)\s+pos:\s*(\d+).+iskey:(\d)\s+type:(.)\s+checksum:([^\s]+).*")
pa = re.compile(r"\[Parsed_ashowinfo.*\]\s+n:\s*(\d+)\s+pts:\s*(\d+)\s+pts_time:\s*(\d+(?:\.\d+)?)\s+pos:\s*(\d+).+nb_samples:(\d+)\s+checksum:([^\s]+).*")



print("Otwieram plik")
with open(filename, "r") as f:
    with open(vlistfilename, "w") as vw, open(alistfilename, "w") as aw:
		for line in f:
			#print(line)
			mv = pv.search(line)
			if(mv):
				n = mv.group(1)
				pts = mv.group(2)
				time = mv.group(3)
				pos = mv.group(4)
				iskey = mv.group(5)
				type = mv.group(6)
				checksum = mv.group(7)
				#print("V N:", n, "PTS:", pts,"t:", time, "pos:", pos, "iskey:", iskey, "type:", type, "checksum:", checksum)
				print("V", n, pts, time, pos, type, iskey, checksum, file=vw, sep=';')

			ma = pa.search(line)
			if(ma):
				n = ma.group(1)
				pts = ma.group(2)
				time = ma.group(3)
				pos = ma.group(4)
				samples = ma.group(5)
				checksum = ma.group(6)
				#print("A N:", n, "PTS:", pts,"t:", time, "pos:", pos, "samples:", samples, "checksum:", checksum)
				print("A", n, pts, time, pos, samples, checksum, file=aw, sep=';')
print("Plik zamknięty")
