import sys

def points(f, count=256):
    xs = (i / (count-1) for i in range(count))
    return [(x, f(x)) for x in xs]


def print_ffmpeg_points(f):
    ps = points(f)
    ts = [f'{x}/{y}' for (x,y) in ps]
    return ' '.join(ts)


def my_curves_function(g=1.0, a=0.0, c=0.0):
    return lambda x: ((1.0-c)*((x + a*(0.5**2-(0.5-x)**2))) \
            + c*(2*(x+(0.5-x)*abs(0.5-x)) - 0.5)) \
            **(1.0/g)

def main(argv):
    if len(argv) >= 4:
        fun = my_curves_function(float(argv[1]), float(argv[2]), float(argv[3]))
    else:
        funbody = argv[1]
        fun = eval('lambda x: (' + funbody +')')
    print(print_ffmpeg_points(fun))


if __name__ == '__main__':
    main(sys.argv)



# x+0.25-(0.5-x)**2
# x + 1.0 * (0.5**2 - (0.5-x)**2)

#f(x) = (x  + 1∙ (0.5^2−(0.5−x)^2))^0.5

# f(x) = x^(1−x)
# f(x) = x∙sin(x∙pi/2)
# f(x) = x + sin(x∙pi)/4
# f(x) = −0.5 +  2∙( x+(0.5−x)∙abs(0.5−x))
# f(x) =( (−0.5 +  2∙( x+(0.5−x)∙abs(0.5−x)))^1.5   ∙x^0.5   ) ^0.5

