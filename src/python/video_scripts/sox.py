# (c) Patryk Czarnik
# Distributed under MIT License. See LICENCE file in the root directory for details.

# This module helps to create sox commands.

from .command_base import *
from .commons import *

from collections.abc import Sequence

class SOX(AbstractCommand):
    def __init__(self, inputs=None, output=None, effects=None):
        super().__init__()
        self.inputs = default_list(inputs)
        self.output = output
        self.effects = default_list(effects, mapper=SOXEffect.of)

    def getScript(self):
        return f'sox {Scriptable.scriptForCollection(self.inputs)} {Scriptable.scriptFor(self.output)} {Scriptable.scriptForCollection(self.effects)}'

    def addEffect(self, effect):
        self.effects.append(SOXEffect.of(effect))


class SOXEffect(Scriptable):
    def __init__(self, name, *args):
        self.name = name
        self.args = default_list(args)

    @staticmethod
    def of(obj):
        if isinstance(obj, SOXEffect):
            return obj
        if isinstance(obj, str):
            return SOXEffect(obj)
        if isinstance(obj, Sequence):
            return SOXEffect(*obj)
        raise TypeError

    def getScript(self):
        return self.name + ' ' + ' '.join(str(arg) for arg in self.args)
