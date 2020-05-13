# LittleSearchEngine
Your very own Little Search Engine

This is a program reads a file that contains all the documents that are to be scanned and then performs a search that returns the top 5 entries based on 2 keywords


Create a text file and call it docs.txt
Then inside docs enter the name of all the files that are to be scanned.
The noisewords file is a file that contains noise words or common words that are to be excluded when scanning the document.  (words such as and, or, I, is, it, on, not count as noisewords)

Enter two keywords that you want to search for and the program returns the top 5 documents that contain the most occurrences of either keywords.  The first document has the most occurrence of either keyword and second contains the second most and etc...

This program makes use of a hash table to keep the document the word occurs in and the number of times it occurs in that document.
