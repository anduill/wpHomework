#!/bin/sh

sbt clean test assembly
#There should only ever be one of these
cp target/scala-2.12/wp_engine_account_resolve-assembly*.jar ./account_resolver.jar