package com.helger.pdflayout.base;

public interface IPLHasOutline
{
  default float getFullTop ()
  {
    return 0;
  }

  default float getFullRight ()
  {
    return 0;
  }

  default float getFullBottom ()
  {
    return 0;
  }

  default float getFullLeft ()
  {
    return 0;
  }

  default float getFullXSum ()
  {
    return 0;
  }

  default float getFullYSum ()
  {
    return 0;
  }
}
