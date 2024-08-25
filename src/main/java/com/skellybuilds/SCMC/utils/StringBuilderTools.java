package com.skellybuilds.SCMC.utils;


import com.skellybuilds.SCMC.SCMC;

public class  StringBuilderTools  {
   public StringBuilderTools(){
   }

   /* THIS CODE NEEDS IMPROVEMENT FROM BOTH MANAGER & THIS - DO NOT USE THIS DOES NOT DO GOOD! */
     public static void insertString(StringBuilder sb, int targetLineNumber, int charIndex, String charToInsert) {
         int currentLineNumber = 1;
         int positionInSB = 0;
         int lineStartPos = 0;

         while (positionInSB < sb.length()) {
             // Find the end of the current line
             int lineEndPos = sb.indexOf(System.lineSeparator(), lineStartPos);
             if (lineEndPos == -1) {
                 lineEndPos = sb.length(); // Handle the last line without a line separator
             }

             if (currentLineNumber == targetLineNumber) {
                 int targetPos = lineStartPos + charIndex;

                 sb.insert(targetPos, charToInsert);

                 break;
             }

             // Move to the next line
             lineStartPos = lineEndPos + System.lineSeparator().length();
             currentLineNumber++;
             positionInSB = lineStartPos;
         }

         if (currentLineNumber < targetLineNumber) {
             sb.append(charToInsert); // it doesn't
         }
     }


 }
