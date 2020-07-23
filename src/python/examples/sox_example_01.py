from video_scripts.sox import SOX

cmd1 = SOX(inputs='in1.flac', output='out.mp3', effects='swap')
print(cmd1.cmd_string())

cmd2 = SOX(inputs='in2.flac', output='out.mp3', effects=['swap', 'deemph'])
print(cmd2.cmd_string())
