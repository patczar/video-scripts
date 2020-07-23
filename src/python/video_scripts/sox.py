# (c) Patryk Czarnik
# Distributed under MIT License. See LICENCE file in the root directory for details.

# This module helps to create sox commands.

from .command_base import *
from .commons import *

from collections.abc import Sequence


class SOX(ACommand):
    def __init__(self, inputs=None, output=None, effects=None):
        super().__init__()
        self.inputs = default_list(inputs)
        self.output = output
        self.effects = default_list(effects, mapper=SOXEffect.of)

    def cmd(self) -> str:
        return 'sox'

    def options(self) -> t.Sequence[str]:
        return cmd_elements_for(self.inputs, self.output, self.effects)

    def add_effect(self, effect):
        self.effects.append(SOXEffect.of(effect))


class SOXEffect(ACommandFragment):
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

    def cmd_elements(self):
        return [self.name] + [str(arg) for arg in self.args]
