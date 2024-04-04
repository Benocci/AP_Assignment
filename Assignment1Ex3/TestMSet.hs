module TestMSet where

import MultiSet

import Data.Char (toLower)
import Data.List (sort)
import System.IO (hPutStrLn, withFile, IOMode(..))

-- Function to calculate "ciao" of a string
ciao :: String -> String
ciao = sort . map toLower

-- Function to read a text file and create an MSet of words with their ciao
readMSet :: FilePath -> IO (MSet String)
readMSet filePath = do
    content <- readFile filePath
    let wordsWithCiao = foldl (\acc word -> add acc (ciao word)) empty (words content)
    return wordsWithCiao


-- Function to write MSet elements with their multiplicity to a file
writeMSet :: FilePath -> MSet String -> IO ()
writeMSet filePath (MS xs) = withFile filePath WriteMode $ \handle ->
    mapM_ (writeElement handle) xs
    where
        writeElement h (elem, count) = hPutStrLn h $ elem ++ " - " ++ show count

-- Function to check if two multisets have the same elements
sameElements :: Eq a => MSet a -> MSet a -> Bool
sameElements mset1 mset2 = elems mset1 == elems mset2

-- Main function
main :: IO ()
main = do
    -- Step a: Read multisets from files
    m1 <- readMSet "./aux_files/anagram.txt"
    m2 <- readMSet "./aux_files/anagram-s1.txt"
    m3 <- readMSet "./aux_files/anagram-s2.txt"
    m4 <- readMSet "./aux_files/margana2.txt"
    
    -- Step b: Check facts and print corresponding comments
    putStrLn "Facts:"
    putStrLn $ "i. Multisets m1 and m4 are not equal, but they have the same elements: " ++ show (sameElements m1 m4)
    putStrLn $ "ii. Multiset m1 is equal to the union of multisets m2 and m3: " ++ show (m1 == union m2 m3)
    
    -- Step c: Write multisets to files
    writeMSet "./aux_files/anag-out.txt" m1
    writeMSet "./aux_files/gana-out.txt" m4
