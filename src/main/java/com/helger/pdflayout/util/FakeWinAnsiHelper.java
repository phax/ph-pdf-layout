/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.pdflayout.util;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.helger.commons.charset.CCharset;
import com.helger.commons.string.StringHelper;

public class FakeWinAnsiHelper
{
  // Copyright 2012-01-10 PlanBase Inc. & Glen Peterson
  //
  // Licensed under the Apache License, Version 2.0 (the "License");
  // you may not use this file except in compliance with the License.
  // You may obtain a copy of the License at
  //
  // http://www.apache.org/licenses/LICENSE-2.0
  //
  // Unless required by applicable law or agreed to in writing, software
  // distributed under the License is distributed on an "AS IS" BASIS,
  // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  // See the License for the specific language governing permissions and
  // limitations under the License.

  private static final Charset ISO_8859_1 = CCharset.CHARSET_ISO_8859_1_OBJ;
  private static final Character UNICODE_BULLET = Character.valueOf ('\u2022');

  // PDFBox uses an encoding that the PDF spec calls WinAnsiEncoding. The spec
  // says this is
  // Windows Code Page 1252.
  // http://en.wikipedia.org/wiki/Windows-1252
  // It has a lot in common with ISO-8859-1, but it defines some additional
  // characters such as
  // the Euro symbol.
  private static final Map <Character, String> UTF16_TO_WINANSI;
  static
  {
    final Map <Character, String> aTempMap = new HashMap <Character, String> ();

    // 129, 141, 143, 144, and 157 are undefined in WinAnsi.
    // I had mapped A0-FF to 160-255 without noticing that that maps each
    // character to
    // itself, meaning that Unicode and WinAnsii are the same in that range.

    // Unicode characters with exact WinAnsi equivalents
    // OE - LATIN CAPITAL LIGATURE OE
    aTempMap.put (Character.valueOf ('\u0152'), new String (new byte [] { 0, (byte) 140 }, ISO_8859_1));
    // oe - LATIN SMALL LIGATURE OE
    aTempMap.put (Character.valueOf ('\u0153'), new String (new byte [] { 0, (byte) 156 }, ISO_8859_1));
    // S Acron - LATIN CAPITAL LETTER S WITH CARON
    aTempMap.put (Character.valueOf ('\u0160'), new String (new byte [] { 0, (byte) 138 }, ISO_8859_1));
    // s acron - LATIN SMALL LETTER S WITH CARON
    aTempMap.put (Character.valueOf ('\u0161'), new String (new byte [] { 0, (byte) 154 }, ISO_8859_1));
    // Y Diaeresis
    aTempMap.put (Character.valueOf ('\u0178'), new String (new byte [] { 0, (byte) 159 }, ISO_8859_1));
    // Capital Z-caron
    aTempMap.put (Character.valueOf ('\u017D'), new String (new byte [] { 0, (byte) 142 }, ISO_8859_1));
    // Lower-case Z-caron
    aTempMap.put (Character.valueOf ('\u017E'), new String (new byte [] { 0, (byte) 158 }, ISO_8859_1));
    // F with a hook (like jf put together)
    aTempMap.put (Character.valueOf ('\u0192'), new String (new byte [] { 0, (byte) 131 }, ISO_8859_1));
    // circumflex (up-caret)
    aTempMap.put (Character.valueOf ('\u02C6'), new String (new byte [] { 0, (byte) 136 }, ISO_8859_1));
    // Tilde
    aTempMap.put (Character.valueOf ('\u02DC'), new String (new byte [] { 0, (byte) 152 }, ISO_8859_1));

    // Cyrillic letters map to their closest Romanizations according to ISO
    // 9:1995
    // http://en.wikipedia.org/wiki/ISO_9
    // http://en.wikipedia.org/wiki/A_(Cyrillic)

    // Cyrillic extensions
    // 0400 Ѐ Cyrillic capital letter IE WITH GRAVE
    // ≡ 0415 Е 0300 (left-accent)
    aTempMap.put (Character.valueOf ('\u0400'), new String (new byte [] { 0, (byte) 200 }, ISO_8859_1));
    // 0401 Ё Cyrillic capital letter IO
    // ≡ 0415 Е 0308 (diuresis)
    aTempMap.put (Character.valueOf ('\u0401'), new String (new byte [] { 0, (byte) 203 }, ISO_8859_1));
    // 0402 Ђ Cyrillic capital letter DJE
    aTempMap.put (Character.valueOf ('\u0402'), new String (new byte [] { 0, (byte) 208 }, ISO_8859_1));
    // 0403 Ѓ Cyrillic capital letter GJE
    // ≡ 0413 Г 0301 (accent)
    // Ghe only maps to G-acute, which is not in our charset.
    // 0404 Є Cyrillic capital letter UKRAINIAN IE
    aTempMap.put (Character.valueOf ('\u0404'), new String (new byte [] { 0, (byte) 202 }, ISO_8859_1));
    // 0405 Ѕ Cyrillic capital letter DZE
    aTempMap.put (Character.valueOf ('\u0405'), "S"); //
    // 0406 І Cyrillic capital letter BYELORUSSIAN-
    // UKRAINIAN I
    // → 0049 I latin capital letter i
    // → 0456 і cyrillic small letter byelorussian-
    // ukrainian i
    // → 04C0 Ӏ cyrillic letter palochka
    aTempMap.put (Character.valueOf ('\u0406'), new String (new byte [] { 0, (byte) 204 }, ISO_8859_1));
    // 0407 Ї Cyrillic capital letter YI
    // ≡ 0406 І 0308 (diuresis)
    aTempMap.put (Character.valueOf ('\u0407'), new String (new byte [] { 0, (byte) 207 }, ISO_8859_1));
    // 0408 Ј Cyrillic capital letter JE
    // 0409 Љ Cyrillic capital letter LJE
    // 040A Њ Cyrillic capital letter NJE
    // 040B Ћ Cyrillic capital letter TSHE
    // 040C Ќ Cyrillic capital letter KJE
    // ≡ 041A К 0301 (accent)
    // 040D Ѝ Cyrillic capital letter I WITH GRAVE
    // ≡ 0418 И 0300 (accent)
    // 040E Ў Cyrillic capital letter SHORT U
    // ≡ 0423 У 0306 (accent)
    // 040F Џ Cyrillic capital letter DZHE

    // Basic Russian alphabet
    // See: http://www.unicode.org/charts/PDF/U0400.pdf
    // 0410 А Cyrillic capital letter A => Latin A
    aTempMap.put (Character.valueOf ('\u0410'), "A");
    // 0411 Б Cyrillic capital letter BE => Latin B
    // → 0183 ƃ latin small letter b with topbar
    aTempMap.put (Character.valueOf ('\u0411'), "B");
    // 0412 В Cyrillic capital letter VE => Latin V
    aTempMap.put (Character.valueOf ('\u0412'), "V");
    // 0413 Г Cyrillic capital letter GHE => Latin G
    aTempMap.put (Character.valueOf ('\u0413'), "G");
    // 0414 Д Cyrillic capital letter DE => Latin D
    aTempMap.put (Character.valueOf ('\u0414'), "D");
    // 0415 Е Cyrillic capital letter IE => Latin E
    aTempMap.put (Character.valueOf ('\u0415'), "E");
    // 0416 Ж Cyrillic capital letter ZHE => Z-caron
    aTempMap.put (Character.valueOf ('\u0416'), new String (new byte [] { 0, (byte) 142 }, ISO_8859_1));
    // 0417 З Cyrillic capital letter ZE => Latin Z
    aTempMap.put (Character.valueOf ('\u0417'), "Z");
    // 0418 И Cyrillic capital letter I => Latin I
    aTempMap.put (Character.valueOf ('\u0418'), "I");
    // 0419 Й Cyrillic capital letter SHORT I => Latin J
    // ≡ 0418 И 0306 (a little mark)
    // The two-character form (reversed N plus the mark) is not supported.
    aTempMap.put (Character.valueOf ('\u0419'), "J");
    // 041A К Cyrillic capital letter KA => Latin K
    aTempMap.put (Character.valueOf ('\u041A'), "K");
    // 041B Л Cyrillic capital letter EL => Latin L
    aTempMap.put (Character.valueOf ('\u041B'), "L");
    // 041C М Cyrillic capital letter EM => Latin M
    aTempMap.put (Character.valueOf ('\u041C'), "M");
    // 041D Н Cyrillic capital letter EN => Latin N
    aTempMap.put (Character.valueOf ('\u041D'), "N");
    // 041E О Cyrillic capital letter O => Latin O
    aTempMap.put (Character.valueOf ('\u041E'), "O");
    // 041F П Cyrillic capital letter PE => Latin P
    aTempMap.put (Character.valueOf ('\u041F'), "P");
    // 0420 Р Cyrillic capital letter ER => Latin R
    aTempMap.put (Character.valueOf ('\u0420'), "R");
    // 0421 С Cyrillic capital letter ES => Latin S
    aTempMap.put (Character.valueOf ('\u0421'), "S");
    // 0422 Т Cyrillic capital letter TE => Latin T
    aTempMap.put (Character.valueOf ('\u0422'), "T");
    // 0423 У Cyrillic capital letter U => Latin U
    // → 0478 Ѹ cyrillic capital letter uk
    // → 04AF ү cyrillic small letter straight u
    // → A64A Ꙋ cyrillic capital letter monograph uk
    aTempMap.put (Character.valueOf ('\u0423'), "U");
    // Is this right?
    aTempMap.put (Character.valueOf ('\u0478'), "U");
    // Is this right?
    aTempMap.put (Character.valueOf ('\u04AF'), "U");
    // Is this right?
    aTempMap.put (Character.valueOf ('\uA64A'), "U");
    // 0424 Ф Cyrillic capital letter EF => Latin F
    aTempMap.put (Character.valueOf ('\u0424'), "F");
    // 0425 Х Cyrillic capital letter HA => Latin H
    aTempMap.put (Character.valueOf ('\u0425'), "H");
    // 0426 Ц Cyrillic capital letter TSE => Latin C
    aTempMap.put (Character.valueOf ('\u0426'), "C");
    // 0427 Ч Cyrillic capital letter CHE => Mapping to "Ch" because there is
    // no
    // C-caron - hope this is the best choice! A also had this as "CH" but
    // some make it
    // Tch as in Tchaikovsky, really didn't know what to do here.
    aTempMap.put (Character.valueOf ('\u0427'), "Ch");
    // 0428 Ш Cyrillic capital letter SHA => S-caron
    aTempMap.put (Character.valueOf ('\u0428'), new String (new byte [] { 0, (byte) 138 }, ISO_8859_1));
    // 0429 Щ Cyrillic capital letter SHCHA => Latin "Shch" because there is
    // no
    // S-circumflex to map it to. Should it go to S-caron like SHA?
    aTempMap.put (Character.valueOf ('\u0429'), "Shch");
    // 042A Ъ Cyrillic capital letter HARD SIGN => Latin double prime, or in
    // this case,
    // right double-quote.
    aTempMap.put (Character.valueOf ('\u042A'), new String (new byte [] { 0, (byte) 148 }, ISO_8859_1));
    // 042B Ы Cyrillic capital letter YERU => Latin Y
    aTempMap.put (Character.valueOf ('\u042B'), "Y");
    // 042C Ь Cyrillic capital letter SOFT SIGN => Latin prime, or in this
    // case,
    // the right-single-quote.
    aTempMap.put (Character.valueOf ('\u042C'), new String (new byte [] { 0, (byte) 146 }, ISO_8859_1));
    // 042D Э Cyrillic capital letter E => Latin E-grave
    aTempMap.put (Character.valueOf ('\u042D'), new String (new byte [] { 0, (byte) 200 }, ISO_8859_1));
    // 042E Ю Cyrillic capital letter YU => Latin U-circumflex
    aTempMap.put (Character.valueOf ('\u042E'), new String (new byte [] { 0, (byte) 219 }, ISO_8859_1));
    // 042F Я Cyrillic capital letter YA => A-circumflex
    aTempMap.put (Character.valueOf ('\u042F'), new String (new byte [] { 0, (byte) 194 }, ISO_8859_1));
    // 0430 а Cyrillic small letter A
    aTempMap.put (Character.valueOf ('\u0430'), "a");
    // 0431 б Cyrillic small letter BE
    aTempMap.put (Character.valueOf ('\u0431'), "b");
    // 0432 в Cyrillic small letter VE
    aTempMap.put (Character.valueOf ('\u0432'), "v");
    // 0433 г Cyrillic small letter GHE
    aTempMap.put (Character.valueOf ('\u0433'), "g");
    // 0434 д Cyrillic small letter DE
    aTempMap.put (Character.valueOf ('\u0434'), "d");
    // 0435 е Cyrillic small letter IE
    aTempMap.put (Character.valueOf ('\u0435'), "e");
    // 0436 ж Cyrillic small letter ZHE
    aTempMap.put (Character.valueOf ('\u0436'), new String (new byte [] { 0, (byte) 158 }, ISO_8859_1));
    // 0437 з Cyrillic small letter ZE
    aTempMap.put (Character.valueOf ('\u0437'), "z");
    // 0438 и Cyrillic small letter I
    aTempMap.put (Character.valueOf ('\u0438'), "i");
    // 0439 й Cyrillic small letter SHORT I
    // ≡ 0438 и 0306 (accent)
    aTempMap.put (Character.valueOf ('\u0439'), "j");
    // 043A к Cyrillic small letter KA
    aTempMap.put (Character.valueOf ('\u043A'), "k");
    // 043B л Cyrillic small letter EL
    aTempMap.put (Character.valueOf ('\u043B'), "l");
    // 043C м Cyrillic small letter EM
    aTempMap.put (Character.valueOf ('\u043C'), "m");
    // 043D н Cyrillic small letter EN
    aTempMap.put (Character.valueOf ('\u043D'), "n");
    // 043E о Cyrillic small letter O
    aTempMap.put (Character.valueOf ('\u043E'), "o");
    // 043F п Cyrillic small letter PE
    aTempMap.put (Character.valueOf ('\u043F'), "p");
    // 0440 р Cyrillic small letter ER
    aTempMap.put (Character.valueOf ('\u0440'), "r");
    // 0441 с Cyrillic small letter ES
    aTempMap.put (Character.valueOf ('\u0441'), "s");
    // 0442 т Cyrillic small letter TE
    aTempMap.put (Character.valueOf ('\u0442'), "t");
    // 0443 у Cyrillic small letter U
    aTempMap.put (Character.valueOf ('\u0443'), "u");
    // 0444 ф Cyrillic small letter EF
    aTempMap.put (Character.valueOf ('\u0444'), "f");
    // 0445 х Cyrillic small letter HA
    aTempMap.put (Character.valueOf ('\u0445'), "h");
    // 0446 ц Cyrillic small letter TSE
    aTempMap.put (Character.valueOf ('\u0446'), "c");
    // 0447 ч Cyrillic small letter CHE - see notes on capital letter.
    aTempMap.put (Character.valueOf ('\u0447'), "ch");
    // 0448 ш Cyrillic small letter SHA
    aTempMap.put (Character.valueOf ('\u0448'), new String (new byte [] { 0, (byte) 154 }, ISO_8859_1));
    // 0449 щ Cyrillic small letter SHCHA
    aTempMap.put (Character.valueOf ('\u0449'), "shch");
    // 044A ъ Cyrillic small letter HARD SIGN
    aTempMap.put (Character.valueOf ('\u044A'), new String (new byte [] { 0, (byte) 148 }, ISO_8859_1));
    // 044B ы Cyrillic small letter YERU
    // → A651 ꙑ cyrillic small letter yeru with back yer
    aTempMap.put (Character.valueOf ('\u044B'), "y");
    // 044C ь Cyrillic small letter SOFT SIGN
    // → 0185 ƅ latin small letter tone six
    // → A64F ꙏ cyrillic small letter neutral yer
    aTempMap.put (Character.valueOf ('\u044C'), new String (new byte [] { 0, (byte) 146 }, ISO_8859_1));
    // 044D э Cyrillic small letter E
    aTempMap.put (Character.valueOf ('\u044D'), new String (new byte [] { 0, (byte) 232 }, ISO_8859_1));
    // 044E ю Cyrillic small letter YU
    // → A655 ꙕ cyrillic small letter reversed yu
    aTempMap.put (Character.valueOf ('\u044E'), new String (new byte [] { 0, (byte) 251 }, ISO_8859_1));
    // is this right?
    aTempMap.put (Character.valueOf ('\uA655'), new String (new byte [] { 0, (byte) 251 }, ISO_8859_1));
    // 044F я Cyrillic small letter YA => a-circumflex
    aTempMap.put (Character.valueOf ('\u044F'), new String (new byte [] { 0, (byte) 226 }, ISO_8859_1));

    // Cyrillic extensions
    // 0450 ѐ CYRILLIC SMALL LETTER IE WITH GRAVE
    // • Macedonian
    // ≡ 0435 е 0300 $̀
    // e-grave => e-grave
    aTempMap.put (Character.valueOf ('\u0450'), new String (new byte [] { 0, (byte) 232 }, ISO_8859_1));
    // 0451 ё CYRILLIC SMALL LETTER IO
    // • Russian, ...
    // ≡ 0435 е 0308 $̈
    aTempMap.put (Character.valueOf ('\u0451'), new String (new byte [] { 0, (byte) 235 }, ISO_8859_1));
    // 0452 ђ CYRILLIC SMALL LETTER DJE
    // • Serbian
    // → 0111 đ latin small letter d with stroke
    aTempMap.put (Character.valueOf ('\u0452'), new String (new byte [] { 0, (byte) 240 }, ISO_8859_1));
    // 0453 ѓ CYRILLIC SMALL LETTER GJE - only maps to g-acute, which is not
    // in our charset.
    // • Macedonian
    // ≡ 0433 г 0301 $́
    // 0454 є CYRILLIC SMALL LETTER UKRAINIAN IE
    // = Old Cyrillic yest
    aTempMap.put (Character.valueOf ('\u0454'), new String (new byte [] { 0, (byte) 234 }, ISO_8859_1));
    // 0455 ѕ CYRILLIC SMALL LETTER DZE
    // • Macedonian
    // → A643 ꙃ cyrillic small letter dzelo
    aTempMap.put (Character.valueOf ('\u0455'), "s");
    // 0456 CYRILLIC SMALL LETTER BYELORUSSIAN-
    // UKRAINIAN I
    // = Old Cyrillic i
    aTempMap.put (Character.valueOf ('\u0456'), new String (new byte [] { 0, (byte) 236 }, ISO_8859_1));
    // 0457 ї CYRILLIC SMALL LETTER YI
    // • Ukrainian
    // ≡ 0456 і 0308 $̈
    aTempMap.put (Character.valueOf ('\u0457'), new String (new byte [] { 0, (byte) 239 }, ISO_8859_1));
    // 0458 ј CYRILLIC SMALL LETTER JE
    // • Serbian, Azerbaijani, Altay
    // 0459 љ CYRILLIC SMALL LETTER LJE
    // • Serbian, Macedonian
    // → 01C9 lj latin small letter lj
    // 045A њ CYRILLIC SMALL LETTER NJE
    // • Serbian, Macedonian
    // → 01CC nj latin small letter nj
    // 045B ћ CYRILLIC SMALL LETTER TSHE
    // • Serbian
    // → 0107 ć latin small letter c with acute
    // → 0127 ħ latin small letter h with stroke
    // → 040B Ћ cyrillic capital letter tshe
    // → 210F ħ planck constant over two pi
    // → A649 ꙉ cyrillic small letter djerv
    // 045C ќ CYRILLIC SMALL LETTER KJE
    // • Macedonian
    // ≡ 043A к 0301 $́
    // 045D ѝ CYRILLIC SMALL LETTER I WITH GRAVE
    // • Macedonian, Bulgarian
    // ≡ 0438 и 0300 $̀
    // 045E ў CYRILLIC SMALL LETTER SHORT U
    // • Byelorussian, Uzbek
    // ≡ 0443 у 0306 $̆
    // 045F џ CYRILLIC SMALL LETTER DZHE
    // • Serbian, Macedonian, Abkhasian
    // → 01C6 dž latin small letter dz with caron

    // Extended Cyrillic
    // ...
    // 0490 Ґ CYRILLIC CAPITAL LETTER GHE WITH UPTURN => G ?
    // Ghe with upturn
    aTempMap.put (Character.valueOf ('\u0490'), "G");
    // 0491 ґ CYRILLIC SMALL LETTER GHE WITH UPTURN
    // • Ukrainian
    aTempMap.put (Character.valueOf ('\u0491'), "g");

    // Other commonly-used unicode characters with exact WinAnsi equivalents
    // En-dash
    aTempMap.put (Character.valueOf ('\u2013'), new String (new byte [] { 0, (byte) 150 }, ISO_8859_1));
    // Em-dash
    aTempMap.put (Character.valueOf ('\u2014'), new String (new byte [] { 0, (byte) 151 }, ISO_8859_1));
    // Curved single open quote
    aTempMap.put (Character.valueOf ('\u2018'), new String (new byte [] { 0, (byte) 145 }, ISO_8859_1));
    // Curved single close-quote
    aTempMap.put (Character.valueOf ('\u2019'), new String (new byte [] { 0, (byte) 146 }, ISO_8859_1));
    // Low single curved-quote
    aTempMap.put (Character.valueOf ('\u201A'), new String (new byte [] { 0, (byte) 130 }, ISO_8859_1));
    // Curved double open quote
    aTempMap.put (Character.valueOf ('\u201C'), new String (new byte [] { 0, (byte) 147 }, ISO_8859_1));
    // Curved double close-quote
    aTempMap.put (Character.valueOf ('\u201D'), new String (new byte [] { 0, (byte) 148 }, ISO_8859_1));
    // Low right double quote.
    aTempMap.put (Character.valueOf ('\u201E'), new String (new byte [] { 0, (byte) 132 }, ISO_8859_1));
    // Dagger
    aTempMap.put (Character.valueOf ('\u2020'), new String (new byte [] { 0, (byte) 134 }, ISO_8859_1));
    // Double dagger
    aTempMap.put (Character.valueOf ('\u2021'), new String (new byte [] { 0, (byte) 135 }, ISO_8859_1));
    // Bullet - use this as replacement character.
    aTempMap.put (UNICODE_BULLET, new String (new byte [] { 0, (byte) 149 }, ISO_8859_1));
    // Ellipsis
    aTempMap.put (Character.valueOf ('\u2026'), new String (new byte [] { 0, (byte) 133 }, ISO_8859_1));
    // Permille
    aTempMap.put (Character.valueOf ('\u2030'), new String (new byte [] { 0, (byte) 137 }, ISO_8859_1));
    // Left angle-quote
    aTempMap.put (Character.valueOf ('\u2039'), new String (new byte [] { 0, (byte) 139 }, ISO_8859_1));
    // Right angle-quote
    aTempMap.put (Character.valueOf ('\u203A'), new String (new byte [] { 0, (byte) 155 }, ISO_8859_1));
    // Euro symbol
    aTempMap.put (Character.valueOf ('\u20ac'), new String (new byte [] { 0, (byte) 128 }, ISO_8859_1));
    // Trademark symbol
    aTempMap.put (Character.valueOf ('\u2122'), new String (new byte [] { 0, (byte) 153 }, ISO_8859_1));

    UTF16_TO_WINANSI = Collections.unmodifiableMap (aTempMap);
  }

  @Nullable
  public static String convertJavaStringToWinAnsi2 (@Nullable final String sIn)
  {
    if (StringHelper.hasNoText (sIn))
      return sIn;

    final StringBuilder aSB = new StringBuilder (sIn.length ());
    for (final char c : sIn.toCharArray ())
    {
      if (c >= 0 && c <= 255)
      {
        aSB.append (c);
      }
      else
      {
        String s = UTF16_TO_WINANSI.get (Character.valueOf (c));

        // "In WinAnsiEncoding, all unused codes greater than 40 map to the bullet character."
        // source: PDF spec, Annex D.3 PDFDocEncoding Character Set p. 656
        // footnote about WinAnsiEncoding.
        if (s == null)
          s = UTF16_TO_WINANSI.get (UNICODE_BULLET);

        aSB.append (s);
      }
    }
    return aSB.toString ();
  }
}
