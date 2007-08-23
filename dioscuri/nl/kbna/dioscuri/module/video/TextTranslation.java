package nl.kbna.dioscuri.module.video;

public class TextTranslation
{

	// Attributes
	
    // ASCII index to Unicode character table
	// Note: the output device using the translated character must support unicode, 
	// else some characters will be displayed incorrectly.
	// Behind some characters "Unicode difference" is written. These ASCII characters have 
	// different indices in unicode compared to ASCII.
    protected String[] asciiToUnicode = new String[] {
    					// Regular ASCII set (0 -127), from 0 to 31 are also commands
    						/* 000 */ "",	// Empty
    						/* 001 */ "?",
    						/* 002 */ "?",
    						/* 003 */ "?",
    						/* 004 */ "?",
    						/* 005 */ "?",
    						/* 006 */ "?",
    						/* 007 */ "?",
    						/* 008 */ "?",
    						/* 009 */ "?",
    						/* 010 */ "",	// Empty
    						/* 011 */ "?",
    						/* 012 */ "?",
    						/* 013 */ "",	// Empty
    						/* 014 */ "?",
    						/* 015 */ "?",
    						/* 016 */ "?",
    						/* 017 */ "?",
    						/* 018 */ "?",
    						/* 019 */ "?",
    						/* 020 */ "�",
    						/* 021 */ "�",
    						/* 022 */ "",	// ?
    						/* 023 */ "",
    						/* 024 */ "?",
    						/* 025 */ "?",
    						/* 026 */ "?",
    						/* 027 */ "?",
    						/* 028 */ "",	// ?
    						/* 029 */ "?",
    						/* 030 */ "?",
    						/* 031 */ "?",
    						/* 032 */ " ",
    						/* 033 */ "!",
    						/* 034 */ "\"",
    						/* 035 */ "#",
    						/* 036 */ "$",
    						/* 037 */ "%",
    						/* 038 */ "&",
    						/* 039 */ "'",
    						/* 040 */ "(",
    						/* 041 */ ")",
    						/* 042 */ "*",
    						/* 043 */ "+",
    						/* 044 */ ",",
    						/* 045 */ "-",
    						/* 046 */ ".",
    						/* 047 */ "/",
    						/* 048 */ "0",
    						/* 049 */ "1",
    						/* 050 */ "2",
    						/* 051 */ "3",
    						/* 052 */ "4",
    						/* 053 */ "5",
    						/* 054 */ "6",
    						/* 055 */ "7",
    						/* 056 */ "8",
    						/* 057 */ "9",
    						/* 058 */ ":",
    						/* 059 */ ";",
    						/* 060 */ "<",
    						/* 061 */ "=",
    						/* 062 */ ">",
    						/* 063 */ "?",
    						/* 064 */ "@",
    						/* 065 */ "A",
    						/* 066 */ "B",
    						/* 067 */ "C",
    						/* 068 */ "D",
    						/* 069 */ "E",
    						/* 070 */ "F",
    						/* 071 */ "G",
    						/* 072 */ "H",
    						/* 073 */ "I",
    						/* 074 */ "J",
    						/* 075 */ "K",
    						/* 076 */ "L",
    						/* 077 */ "M",
    						/* 078 */ "N",
    						/* 079 */ "O",
    						/* 080 */ "P",
    						/* 081 */ "Q",
    						/* 082 */ "R",
    						/* 083 */ "S",
    						/* 084 */ "T",
    						/* 085 */ "U",
    						/* 086 */ "V",
    						/* 087 */ "W",
    						/* 088 */ "X",
    						/* 089 */ "Y",
    						/* 090 */ "Z",
    						/* 091 */ "[",
    						/* 092 */ "\\",
    						/* 093 */ "]",
    						/* 094 */ "^",
    						/* 095 */ "_",
    						/* 096 */ "`",
    						/* 097 */ "a",
    						/* 098 */ "b",
    						/* 099 */ "c",
    						/* 100 */ "d",
    						/* 101 */ "e",
    						/* 102 */ "f",
    						/* 103 */ "g",
    						/* 104 */ "h",
    						/* 105 */ "i",
    						/* 106 */ "j",
    						/* 107 */ "k",
    						/* 108 */ "l",
    						/* 109 */ "m",
    						/* 110 */ "n",
    						/* 111 */ "o",
    						/* 112 */ "p",
    						/* 113 */ "q",
    						/* 114 */ "r",
    						/* 115 */ "s",
    						/* 116 */ "t",
    						/* 117 */ "u",
    						/* 118 */ "v",
    						/* 119 */ "w",
    						/* 120 */ "x",
    						/* 121 */ "y",
    						/* 122 */ "z",
    						/* 123 */ "{",
    						/* 124 */ "|",
    						/* 125 */ "}",
    						/* 126 */ "~",
    						/* 127 */ "?",
    						
    				    // Extended ASCII set (128 - 255)
    						/* 128 */ "�",
    						/* 129 */ "�",
    						/* 130 */ "�",
    						/* 131 */ "�",
    						/* 132 */ "�",
    						/* 133 */ "�",
    						/* 134 */ "�",
    						/* 135 */ "�",
    						/* 136 */ "�",
    						/* 137 */ "�",
    						/* 138 */ "�",
    						/* 139 */ "�",
    						/* 140 */ "�",
    						/* 141 */ "�",
    						/* 142 */ "�",
    						/* 143 */ "�",
    						/* 144 */ "�",
    						/* 145 */ "�",
    						/* 146 */ "�",
    						/* 147 */ "�",
    						/* 148 */ "�",
    						/* 149 */ "�",
    						/* 150 */ "�",
    						/* 151 */ "�",
    						/* 152 */ "�",
    						/* 153 */ "�",
    						/* 154 */ "�",
    						/* 155 */ "�",
    						/* 156 */ "�",
    						/* 157 */ "�",
    						/* 158 */ "�",
    						/* 159 */ "�",
    						/* 160 */ "�",
    						/* 161 */ "�",
    						/* 162 */ "�",
    						/* 163 */ "�",
    						/* 164 */ "�",
    						/* 165 */ "�",
    						/* 166 */ "",	// ?
    						/* 167 */ "",	// ?
    						/* 168 */ "�",
    						/* 169 */ "�",
    						/* 170 */ "�",
    						/* 171 */ "�",
    						/* 172 */ "�",
    						/* 173 */ "�",
    						/* 174 */ "�",
    						/* 175 */ "�",
    						/* 176 */ "?",	// Unicode difference
    						/* 177 */ "?",	// Unicode difference
    						/* 178 */ "?",	// Unicode difference
    						/* 179 */ "?",	// Unicode difference
    						/* 180 */ "?",	// Unicode difference
    						/* 181 */ "�",
    						/* 182 */ "�",
    						/* 183 */ "�",
    						/* 184 */ "�",
    						/* 185 */ "?",	// Unicode difference
    						/* 186 */ "?",	// Unicode difference
    						/* 187 */ "?",	// Unicode difference
    						/* 188 */ "?",	// Unicode difference
    						/* 189 */ "",	// ?
    						/* 190 */ "�",
    						/* 191 */ "?",	// Unicode difference
    						/* 192 */ "?",	// Unicode difference
    						/* 193 */ "?",	// Unicode difference
    						/* 194 */ "?",	// Unicode difference
    						/* 195 */ "?",	// Unicode difference
    						/* 196 */ "?",	// Unicode difference
    						/* 197 */ "?",	// Unicode difference
    						/* 198 */ "�",
    						/* 199 */ "�",
    						/* 200 */ "?",	// Unicode difference
    						/* 201 */ "?",	// Unicode difference
    						/* 202 */ "?",	// Unicode difference
    						/* 203 */ "?",	// Unicode difference
    						/* 204 */ "?",	// Unicode difference
    						/* 205 */ "?",	// Unicode difference
    						/* 206 */ "?",	// Unicode difference
    						/* 207 */ "�",
    						/* 208 */ "�",
    						/* 209 */ "�",
    						/* 210 */ "�",
    						/* 211 */ "�",
    						/* 212 */ "�",
    						/* 213 */ "�",	// ?
    						/* 214 */ "�",
    						/* 215 */ "�",
    						/* 216 */ "�",
    						/* 217 */ "?",	// Unicode difference
    						/* 218 */ "?",	// Unicode difference
    						/* 219 */ "?",	// Unicode difference
    						/* 220 */ "?",	// Unicode difference
    						/* 221 */ "�",
    						/* 222 */ "�",
    						/* 223 */ "?",	// Unicode difference
    						/* 224 */ "�",
    						/* 225 */ "�",
    						/* 226 */ "�",
    						/* 227 */ "�",
    						/* 228 */ "�",
    						/* 229 */ "�",
    						/* 230 */ "�",
    						/* 231 */ "�",
    						/* 232 */ "�",
    						/* 233 */ "�",
    						/* 234 */ "�",
    						/* 235 */ "�",
    						/* 236 */ "�",
    						/* 237 */ "�",
    						/* 238 */ "�",
    						/* 239 */ "�",
    						/* 240 */ "�",
    						/* 241 */ "�",
    						/* 242 */ "",	// ?
    						/* 243 */ "�",
    						/* 244 */ "�",
    						/* 245 */ "�",
    						/* 246 */ "�",
    						/* 247 */ "�",
    						/* 248 */ "�",
    						/* 249 */ "�",
    						/* 250 */ "�",
    						/* 251 */ "�",
    						/* 252 */ "�",
    						/* 253 */ "�",
    						/* 254 */ "?",	// Unicode difference
    						/* 255 */ ""	// ?
    					};
	
	
}
