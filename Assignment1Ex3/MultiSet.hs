-- Module to export the MSet, its costructor and all the functions
module MultiSet (
    MSet(..),
    empty,
    add,
    occs,
    elems,
    subeq,
    union,
    mapMSet
    ) where

-- Definition of the MSet type constructor
data MSet a = MS [(a, Int)] deriving (Show)

-- Instance of Eq for MSet
-- Make the equality comparison for two multisets
instance Eq a => Eq (MSet a) where
    (==) :: Eq a => MSet a -> MSet a -> Bool
    (MS a) == (MS b) = all (\(x,n) -> n == occs (MS b) x) a &&
                       all (\(x,n) -> n == occs (MS a) x) b

-- Instance Foldable for MSet
-- Folding a multiset with a binary function
instance Foldable MSet where
    foldr :: (a -> b -> b) -> b -> MSet a -> b
    foldr f z (MS xs) = foldr (\(x, _) acc -> f x acc) z xs


-- Constructor for an empty multiset
empty :: MSet a
empty = MS []

-- Add an element to a multiset
add :: Eq a => MSet a -> a -> MSet a
add (MS xs) v = MS (addToSet xs v)
    where
        -- Helper function to add element to the set
        addToSet [] v' = [(v', 1)]
        addToSet ((y,n):ys) v'
            | v' == y = (y, n+1): ys
            | otherwise = (y, n): addToSet ys v'

-- Count occurrences of an element in the multiset
occs :: Eq a => MSet a -> a -> Int
occs (MS xs) v = sum [n | (x,n) <- xs, x == v]

-- Get all elements of a multiset
elems :: MSet a -> [a]
elems (MS xs) = [x | (x, _) <- xs]

-- Check if a multiset is a subset of another multiset
subeq :: Eq a => MSet a -> MSet a -> Bool
subeq (MS []) _ = True
subeq (MS ((x,n):xs)) ys = n <= occs ys x && subeq (MS xs) ys

-- Union of two multiset
union :: Eq a => MSet a -> MSet a -> MSet a
union (MS xs) (MS ys) = foldl (\acc (x,n) -> addSpec acc x n) (MS xs) ys
    where
        -- Helper function to add element with a specified occurrences to a multiset
        addSpec (MS mset) v n = MS (addSpecToSet mset v n)
        addSpecToSet [] v n = [(v, n)]
        addSpecToSet ((y, m'):ys) v n
            | v == y = (y, m' + n):ys
            | otherwise = (y, m'):addSpecToSet ys v n

-- Mapping a function over the elements of a multiset (new version)
mapMSet :: (Eq b) => (a -> b) -> MSet a -> MSet b
mapMSet f (MS xs) = foldl (\acc (x, n) -> addSpec acc (f x) n) empty xs
    where
        -- Helper function to add element with a specified occurrences to a multiset
        addSpec (MS mset) v n = MS (addToSet mset v n)
        addToSet [] v n = [(v, n)]
        addToSet ((y, m'):ys) v n
            | v == y = (y, m' + n):ys
            | otherwise = (y, m'):addToSet ys v n

-- It is not possible to define an instance of Functor for MSet by providing
-- mapMSet as the implementation of fmap because the Functor laws would not be
-- satisfied. If f maps two distinct elements to the same element, the multiplicities
-- would need to be combined, and this violates the principle that fmap should treat
-- elements independently and preserve structure.
-- By definition in the Functor:
-- The mapping operation by itself does not modify the values in the functor, only the
-- function. The structure of the functor remains unchanged and only the values are modified.
-- In the implementation fmap don't have (Eq b)