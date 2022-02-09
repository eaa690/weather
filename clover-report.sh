#!/bin/sh
mvn clean clover:setup test clover:aggregate clover:clover
