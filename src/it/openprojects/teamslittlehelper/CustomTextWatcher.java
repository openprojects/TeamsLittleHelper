package it.openprojects.teamslittlehelper;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class CustomTextWatcher implements TextWatcher {
	
	private EditText mEditText;
	String CurrentWord;

	public CustomTextWatcher(EditText e) {
		  mEditText = e;
	}
	
	private boolean IsValid( CharSequence s ) //this loops checks if the current CharSequence is valid or not
	{
	  for ( int i = 0; i < s.length(); i ++ )  // loop to iterate for each letter
	  {
		//return false if character is not letter or digit or space
		  if ( !Character.isLetterOrDigit( s.charAt(i)) && !Character.isSpace(s.charAt(i)) ) 
			  return false;
	  }
	  return true;
	}
	
	
	
	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		CurrentWord = s.toString();
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		if( !IsValid(s) )
		{
			mEditText.setText(CurrentWord);
		    mEditText.setSelection(start);
		}

	}

}
