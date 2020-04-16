# (c) Patryk Czarnik
# Distributed under MIT License. See LICENCE file in the root directory for details.

# This module helps to create ffmpeg commands.

from .command_base import *
from .commons import *

class FFMPEG(AbstractCommand):
    def __init__(self, inputs=None, outputs=None, filters=None, codecs=None):
        super().__init__()
        self.inputs = default_list(inputs, FFInput.of)
        self.outputs = default_list(outputs, FFOutput.of)
        self.filters = FFFilterGraph.of(filters)
        self.codecs = default_list(codecs)

    def getScript(self):
        return f'ffmpeg {Scriptable.scriptForCollection(self.inputs)} {Scriptable.scriptFor(self.filters)} {Scriptable.scriptForCollection(self.codecs)} {Scriptable.scriptForCollection(self.outputs)}'


class FFInputOutput(Scriptable):
    def __init__(self, file, format=None, **options):
        self.file = file
        self.format = format
        self.options = options

    def getScript(self):
        result = ''
        if self.format:
            result += f'-f {self.format} '
        for option, value in self.options.items():
            result += f'-{option} {value}'
        result += self.specialOptions()
        result += f' {self.before_file()}{self.file}'
        return result

    def addOption(self, name, value):
        self.options[name] = value

    @abstractmethod
    def before_file(self):
        return ''

    def specialOptions(self):
        return ''

    @classmethod
    def of(cls, obj):
        if isinstance(obj, cls): return obj
        if isinstance(obj, str): return cls(obj)


class FFInput(FFInputOutput):
    def __init__(self, file, format=None, **options):
        super().__init__(file, format, **options)

    def before_file(self):
        return '-i '


class FFOutput(FFInputOutput):
    def __init__(self, file, format=None, maps=None, **options):
        super().__init__(file, format, **options)
        self.maps = default_list(maps)

    def before_file(self):
        return ''

    def specialOptions(self):
        return ' '.join(f'-map [{map}]' for map in self.maps)


class FFFilterGraph(Scriptable):
    def __init__(self):
        self.chains = []

    @staticmethod
    def of(obj):
        if obj is None: return None
        elif isinstance(obj, FFFilterGraph): return obj
        elif isinstance(obj, FFFilterChain):
            result = FFFilterGraph()
            result.chains = [obj]
            return result
        else:
            result = FFFilterGraph()
            result.chains = [FFFilterChain().of(obj)]
            return result

    def getScript(self):
        if len(self.chains) == 0:
            return ''
        else:
            return f'-filter_complex "{Scriptable.scriptForCollection(self.chains, ";")}"'

    def addChain(self, chain):
        self.chains.append(chain)
        return self

    def newChain(self, start_label=None, end_label=None):
        chain = FFFilterChain(start_label, end_label)
        self.chains.append(chain)
        return chain


class FFFilterChain(Scriptable):
    def __init__(self, start_labels=None, end_labels=None, steps=None):
        self.start_labels = default_list(start_labels)
        self.end_labels = default_list(end_labels)
        self.steps = default_list(steps)

    @staticmethod
    def of(obj):
        if isinstance(obj, FFFilterChain): return obj
        else: return FFFilterChain(steps=obj)

    def getScript(self):
        result = ''
        for lbl in self.start_labels:
            result += f'[{lbl}]'
        if not self.steps:
            result += 'null'
        else:
            result += Scriptable.scriptForCollection(self.steps, ',')
        for lbl in self.end_labels:
            result += f'[{lbl}]'
        return result

    def addStep(self, step):
        self.steps.append(step)
        return self


class FFFilter(Scriptable):
    def __init__(self, name, *args, **kwargs):
        self.name = name
        self.parameters = []
        for arg in args:
            self.addParameter('', arg)
        for key, arg in kwargs.items():
            self.addParameter(key, arg)

    @staticmethod
    def of(obj):
        if isinstance(obj, FFFilter): return obj
        else: raise TypeError(f'Cannot create FFFilter from a {type(obj)}')

    def getScript(self):
        result = self.name
        if len(self.parameters) > 0:
            result += '='
        for i, (par_name, par_value) in enumerate(self.parameters):
            if i > 0:
                result += ':'
            if par_name:
                result += par_name + '='
            result += FFFilter.escape_parameter_value(par_value)

        return result

    def addParameter(self, name='', value=''):
        self.parameters.append((name, value))
        return self

    ESCAPED_CHARS = {',', ':', ';', '\\'}
    @staticmethod
    def escape_parameter_value(value):
        if(not isinstance(value, str)):
            return str(value)
        if set(value) & FFFilter.ESCAPED_CHARS:
            return ''.join('\\'+ch if ch in FFFilter.ESCAPED_CHARS else ch for ch in value)
        return value

