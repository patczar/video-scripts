# (c) Patryk Czarnik
# Distributed under MIT License. See LICENCE file in the root directory for details.

# This module define base classes for all command-generating classes in the project.

from abc import ABC, abstractmethod, abstractproperty
from collections.abc import Iterable


class Textible(ABC):
    @abstractmethod
    def getText(self):
        return ''

    @staticmethod
    def getTextFor(obj, sep=' '):
        if obj is None: return ''
        if isinstance(obj, str): return obj
        if isinstance(obj, Textible): return obj.getText()
        if isinstance(obj, Iterable): return Textible.getTextForCollection(obj, sep)
        return str(obj)

    @staticmethod
    def getTextForCollection(collection, sep=' '):
        return sep.join(Textible.getTextFor(item) for item in collection)


class AbstractCommand(Textible):
    def run(self):
        pass # TODO

