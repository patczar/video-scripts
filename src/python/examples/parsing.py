from video_scripts.video_processor import *

step = parse_line('# just a comment')
print('1:', step)

step = parse_line('file path/to/file.avi')
print('2:', step)
if isinstance(step, FileSpec):
    print(step.path, step.start, step.end)


step = parse_line('file path/to/file.avi -s 20 -e 44.5')
print('3:', step)
if isinstance(step, FileSpec):
    print(step.path, step.start, step.end)


step = parse_line('set-video -gamma 1.4 -abc Abc ')
print('4:', step)
if isinstance(step, OptionSpec):
    print(step.what, step.params)


step = parse_line('set-video -crop 1792,896,64,128 -scale 1280,640 -gamma 1.4 -speed 4 -textlist a,bc,d')
print('5:', step)
if isinstance(step, OptionSpec):
    print(step.what, step.params)

step = parse_line('set-video -cited "Ala ma kota"')
print('6:', step)
if isinstance(step, OptionSpec):
    print(step.what, step.params)

step = parse_line('set-video -cited "Ala ma kota" -normal Ola')
print('7:', step)
if isinstance(step, OptionSpec):
    print(step.what, step.params)


