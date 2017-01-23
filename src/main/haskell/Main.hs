module Main where

import Lib
import Huffmann

main :: IO ()
main = do
    putStrLn "Enter expression: "
    expression <- getLine
    let root = head . forward . frequences $ expression
    putStrLn $ "Root node: " ++ (show root)
    let m = codeMap . backward . changeDirection $ root
    putStrLn $ "Map: " ++ (show m)
    let result = encode expression m
    putStrLn $ "Result: " ++ (show result)
