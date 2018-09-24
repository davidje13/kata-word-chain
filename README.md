# Word Chain Finder

Implementation of a kata to find word chains between two words.

---

The kata specification is here: http://codekata.com/kata/kata19-word-chains/

## Testing

```sh
./gradlew test
```

## Running

```sh
./gradlew run --args='/usr/share/dict/words cat dog'
```

The chain will be printed to stdout.

Providing only one word will show the longest chain from that word.

Providing no words will show the longest chain in the given dictionary
(this is a slow operation)

An example longest chain is from "apiole" to "chinik" (50 words).

## Profiling

The executable can be profiled using:

```sh
./gradlew installDist && time ./build/install/word-chains/bin/word-chains /usr/share/dict/words cat dog
```
