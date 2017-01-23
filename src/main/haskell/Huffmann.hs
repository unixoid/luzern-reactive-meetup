module Huffmann where
    
import Data.List as List 
import Data.Ord as Ord
import Data.Map (Map, fromList, findWithDefault)

data Node = Node {ch :: [Char], subnodes :: [Node], weight :: Int, bits :: [Char]} deriving (Show)

frequences :: String -> [Node]
frequences s = map (\c -> Node {ch = [c], subnodes = [], weight = length (findIndices (== c) s), bits = []}) $ nub s

forward :: [Node] -> [Node]
forward [node] = [node]
forward nodes =
    let sortedNodes = sortBy (comparing weight) nodes
        [n1, n2] = take 2 sortedNodes
        newNode = Node {ch = (ch n1) ++ (ch n2), subnodes = [n1, n2], weight = weight n1 + weight n2, bits = []}
    in forward (newNode:(drop 2 sortedNodes))

changeDirection :: Node -> Node
changeDirection node = do
    let nodes = subnodes node
    if null nodes 
        then Node {ch = ch node, subnodes = [], weight = 0, bits = ['0']}
        else node

backwardNode :: Node -> [Char] -> Char -> [Node]
backwardNode node bits bit = backward (Node {ch = ch node, subnodes = subnodes node, weight = 0, bits = bits ++ [bit]})

backward :: Node -> [Node]
backward node = do
    let nodes = subnodes node
    if null nodes
        then [node]
        else (backwardNode (nodes !! 0) (bits node) '0') ++ (backwardNode (nodes !! 1) (bits node) '1')

codeMap :: [Node] -> Map Char [Char]
codeMap nodes = fromList $ map (\node -> (head (ch node), bits node)) nodes

encode :: String -> Map Char [Char] -> [[Char]]
encode s m = map (\c -> findWithDefault "ERROR" c m) s

