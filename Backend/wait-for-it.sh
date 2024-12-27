#!/bin/sh

host=$1
port=$2
timeout=${3:-15} # Timeout por defecto de 15 segundos

start_time=$(date +%s)

while ! nc -z "$host" "$port"; do
  sleep 1
  elapsed_time=$(( $(date +%s) - start_time ))

  if [[ $elapsed_time -ge $timeout ]]; then
    echo "Timeout while waiting for $host:$port" >&2
    exit 1
  fi
done

echo "$host:$port is available"
exit 0