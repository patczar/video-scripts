from video_scripts.ffmpeg import FFMPEG

cmd1 = FFMPEG(inputs='in.avi', outputs='out.mp4')

print(cmd1.getScript())
