package com.github.alexeylisyutenko.windowsserviceplugin.script

import spock.lang.Specification

class MultiValueParameterConverterTest extends Specification {

    def "Simple string arguments shouldn't be somehow changed"() {
        expect:
        MultiValueParameterConverter.convertToString('-XX:NewRatio=1#-XX:+UseConcMarkSweepGC') == '-XX:NewRatio=1#-XX:+UseConcMarkSweepGC'
        MultiValueParameterConverter.convertToString('-XX:NewRatio=1;-XX:+UseConcMarkSweepGC') == '-XX:NewRatio=1;-XX:+UseConcMarkSweepGC'
        MultiValueParameterConverter.convertToString('') == ''
    }

    def "Lists of strings should be converted to ; separated string"() {
        expect:
        MultiValueParameterConverter.convertToString(['-XX:NewRatio=1', '-XX:+UseConcMarkSweepGC']) == '-XX:NewRatio=1;-XX:+UseConcMarkSweepGC'
        MultiValueParameterConverter.convertToString([]) == ''
    }

    def "Maps of strings should be converted to ; separated string"() {
        expect:
        MultiValueParameterConverter.convertToString(['envKey1': 'value1', 'envKey2': 'value2', 'envKey3': 'value3']) == 'envKey1=value1;envKey2=value2;envKey3=value3'
        MultiValueParameterConverter.convertToString([:]) == ''
    }

    def "If we use either # or ; character in lists or maps we need to put them inside single quotes"() {
        expect:
        MultiValueParameterConverter.convertToString(['val#ue1', 'val;ue2', '#', ';', 'value3#', 'value4;']) == "val'#'ue1;val';'ue2;'#';';';value3'#';value4';'"
        MultiValueParameterConverter.convertToString(['key#': 'value;']) == "key'#'=value';'"
    }

}
