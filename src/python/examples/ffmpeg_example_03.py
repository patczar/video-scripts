from video_scripts.ffmpeg import FFMPEG, FFFilterGraph, FFFilterChain, FFFilter

cmd1 = FFMPEG(inputs='in1.avi', outputs='out.mp4')

f1 = FFFilter('format', 'yuv420p')
f2 = FFFilter('scale')
f2.add_parameter('width', 800)
f2.add_parameter('height', 600)

f3 = FFFilter('eq', gamma=1.3, contrast=1.05, saturation=1.03)

chain1 = FFFilterChain()
chain1.add_step(f1)
chain1.add_step(f2)
chain1.add_step(f3)
chain1.add_step(FFFilter('select', "'between(n,100,200)'"))

filters = FFFilterGraph()
filters.add_chain(chain1)

cmd1._filters = filters

cmd1._filters.new_chain('AA', 'BB')\
    .add_step(FFFilter('fade', t='out', start='40.0', d=2))\
    .add_step(FFFilter('eq', gamma=1.3, contrast=1.05, saturation=1.03))


print(cmd1.cmd_string())
