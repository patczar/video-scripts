from video_scripts.sox import SOX

cmd1 = SOX(inputs=['in1.flac', 'in2.flac'], output='out.mp3')
print(cmd1.cmd_string())
