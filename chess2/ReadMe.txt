Main inspiration from:
https://www.chessprogramming.org
https://peterellisjones.com/posts/generating-legal-chess-moves-efficiently
https://rhysre.net/2019/01/15/magic-bitboards.html
https://lichess.org/editor
https://en.wikipedia.org/wiki/Chess
http://www.frayn.net/beowulf/theory.html
http://pradu.us/old/Nov27_2008/Buzz/research/magic/Bitboards.pdf
*Notes:
1) since java does not have unsigned numbers, used logical right-shift ">>>"
2) magic numbers generator can be found in folder "main" -> "FindingMagics" (used different seed, forgotten which one, doesnt affect game, any seed can be used)
3) all code which has "square"%8 is used as X coord, and "square"/8 as Y coord