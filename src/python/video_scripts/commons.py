# (c) Patryk Czarnik
# Distributed under MIT License. See LICENCE file in the root directory for details.

# This module contains common helper functions.

from collections.abc import Iterable


def ident(x):
    '''The identity function.'''
    return x

def default_string(value):
    '''
        Produces a string value based on the given value.
        To be used during initialization of object attributes etc.
    '''
    if value is None: return ''
    if isinstance(value, str): return value
    return str(value)


def default_list(value, mapper=ident):
    '''
        Produces a list based on the given value.
        To be used during initialization of object attributes etc.

        If the value is None, than an empty list is returned.
        If the value is an iterable, but not a string, then a new list of the same elements is returned.
        If the value is a string or a non-iterable object, then a singleton list with that element is returned.

        mapper is an optional function that will be applied to each element of the list being returned.
    '''
    if value is None: return []
    if isinstance(value, Iterable) and not isinstance(value, str):
        return [mapper(x) for x in value]
    return [mapper(value)]


