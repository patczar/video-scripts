# (c) Patryk Czarnik
# Distributed under MIT License. See LICENCE file in the root directory for details.

# This module helps to create ffmpeg commands.

from .command_base import *
from .commons import *


class FFMPEG(ACommand):
    def __init__(self, inputs=None, outputs=None, global_options=None, filters=None, codecs=None):
        super().__init__()
        self._inputs = default_list(inputs, FFInput.of)
        self._outputs = default_list(outputs, FFOutput.of)
        self._global_options = default_dict(global_options, mapper2=FFGlobalOption.of)
        self._filters = FFFilterGraph.of(filters)
        self._codecs = default_list(codecs)

    def cmd(self):
        return 'ffmpeg'

    def options(self):
        return cmd_elements_for(self._global_options, self._inputs, self._filters, self._codecs, self._outputs)


class FFGlobalOption(ACommandFragment):
    def __init__(self, name, *parameters):
        self._name = name
        self._parameters = default_list(parameters)

    def cmd_elements(self) -> t.Sequence[str]:
        return ['-' + self._name] + self._parameters

    @staticmethod
    def of(name, value):
        if isinstance(value, Sequence) and not isinstance(value, str):
            return FFGlobalOption(name, *value)
        else:
            return FFGlobalOption(name, value)


class FFInputOutput(ACommandFragment):
    def __init__(self, file, format=None, **options):
        self._file = file
        self._format = format
        self._options = default_dict(options)

    def cmd_elements(self):
        result = []
        if self._format:
            result.append('-f')
            result.append(self._format)
        result.extend(cmd_elements_for(self._options))
        result.extend(cmd_elements_for(self.options_before_file()))
        result.append(self._file)
        return result

    def addOption(self, name, value):
        self._options[name] = value

    @abstractmethod
    def options_before_file(self):
        return []

    @classmethod
    def of(cls, obj):
        if isinstance(obj, cls): return obj
        if isinstance(obj, str): return cls(obj)


class FFInput(FFInputOutput):
    def __init__(self, file, format=None, **options):
        super().__init__(file, format, **options)

    def options_before_file(self):
        return ['-i']


class FFOutput(FFInputOutput):
    def __init__(self, file, format=None, maps=None, **options):
        super().__init__(file, format, **options)
        self._maps = default_list(maps)

    def options_before_file(self):
        result = []
        for m in self._maps:
            result.append('-map')
            result.append(m)
        return result


class FFFilterGraph(ACommandFragment):
    def __init__(self):
        self._chains = []

    @staticmethod
    def of(obj):
        if obj is None: return None
        elif isinstance(obj, FFFilterGraph): return obj
        elif isinstance(obj, FFFilterChain):
            result = FFFilterGraph()
            result._chains = [obj]
            return result
        else:
            result = FFFilterGraph()
            result._chains = [FFFilterChain.of(obj)]
            return result

    def cmd_elements(self) -> t.Sequence[str]:
        if len(self._chains) == 0:
            return []
        else:
            return ['-filter_complex', '"' + ';'.join(chain.cmd_string() for chain in self._chains) + '"']

    def add_chain(self, chain):
        self._chains.append(chain)
        return self

    def new_chain(self, start_label=None, end_label=None):
        chain = FFFilterChain(start_label, end_label)
        self._chains.append(chain)
        return chain


class FFFilterChain:
    def __init__(self, start_labels=None, end_labels=None, steps=None):
        self._start_labels = default_list(start_labels)
        self._end_labels = default_list(end_labels)
        self._steps = default_list(steps)

    @staticmethod
    def of(obj):
        if isinstance(obj, FFFilterChain): return obj
        else: return FFFilterChain(steps=obj)

    def cmd_string(self):
        result = ''
        for lbl in self._start_labels:
            result += f'[{lbl}]'
        if not self._steps:
            result += 'null'
        else:
            result += ','.join(chain.cmd_string() for chain in self._steps)
        for lbl in self._end_labels:
            result += f'[{lbl}]'
        return result

    def add_step(self, step):
        self._steps.append(step)
        return self


class FFFilter:
    def __init__(self, name, *args, **kwargs):
        self._name = name
        self._parameters = []
        for arg in args:
            self.add_parameter('', arg)
        for key, arg in kwargs.items():
            self.add_parameter(key, arg)

    @staticmethod
    def of(obj):
        if isinstance(obj, FFFilter): return obj
        else: raise TypeError(f'Cannot create FFFilter from a {type(obj)}')

    def cmd_string(self):
        result = self._name
        if len(self._parameters) > 0:
            result += '='
        for i, (par_name, par_value) in enumerate(self._parameters):
            if i > 0:
                result += ':'
            if par_name:
                result += par_name + '='
            result += FFFilter.escape_parameter_value(par_value)

        return result

    def add_parameter(self, name='', value=''):
        self._parameters.append((name, value))
        return self


    ESCAPED_CHARS = {',', ':', ';', '\\'}
    @staticmethod
    def escape_parameter_value(value):
        if(not isinstance(value, str)):
            value = str(value)
        if set(value) & FFFilter.ESCAPED_CHARS:
            return ''.join('\\'+ch if ch in FFFilter.ESCAPED_CHARS else ch for ch in value)
        return value

