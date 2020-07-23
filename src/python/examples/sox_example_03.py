from video_scripts.sox import SOX, SOXEffect

cmd1 = SOX(inputs=['in1.flac', 'in2.flac'], output='out.mp3')
print(cmd1.cmd_string())

cmd1.add_effect(SOXEffect('vol', 2.5))
cmd1.add_effect(SOXEffect('trim', 7.0, 12.25))
cmd1.add_effect('swap')
cmd1.add_effect(('pad', 1, 2))
print(cmd1.cmd_string())
