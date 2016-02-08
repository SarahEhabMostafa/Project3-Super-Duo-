package it.jaschke.alexandria;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    public EditText ean;
    private final int LOADER_ID = 1;
    public static final int REQUEST_CODE_SCAN = 10;
    private View rootView;
    private TextView textViewBookTitle, textViewBookSubtitle, textViewAuthors, textViewCategories
            , textViewEmpty;
    private Button buttonScan, buttonSave, buttonDelete;
    private ImageView imageViewBookCover;
    private final String EAN_CONTENT="eanContent";
    public static final String SCAN_FORMAT = "scanFormat";
    public static final String SCAN_CONTENTS = "scanContents";
    private ProgressDialog progressDialog;

    private String mScanFormat = "Format:";
    private String mScanContents = "Contents:";

    public AddBook(){
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(ean!=null) {
            outState.putString(EAN_CONTENT, ean.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        ean = (EditText) rootView.findViewById(R.id.ean);
        textViewBookTitle = (TextView) rootView.findViewById(R.id.bookTitle);
        textViewBookSubtitle = (TextView) rootView.findViewById(R.id.bookSubTitle);
        textViewAuthors = (TextView) rootView.findViewById(R.id.authors);
        textViewCategories = (TextView) rootView.findViewById(R.id.categories);
        buttonScan = (Button) rootView.findViewById(R.id.scan_button);
        buttonSave = (Button) rootView.findViewById(R.id.save_button);
        buttonDelete = (Button) rootView.findViewById(R.id.delete_button);
        imageViewBookCover = (ImageView) rootView.findViewById(R.id.bookCover);
        textViewEmpty = (TextView) rootView.findViewById(R.id.textView_empty);

        progressDialog = new ProgressDialog(getContext());

        ean.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ean =s.toString();
                //catch isbn10 numbers
                if(ean.length()==10 && !ean.startsWith("978")){
                    ean="978"+ean;
                }
                if(ean.length()<13){
                    clearFields();
                    return;
                }
                //Once we have an ISBN, start a book intent
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean);
                bookIntent.setAction(BookService.FETCH_BOOK);
                getActivity().startService(bookIntent);
                AddBook.this.restartLoader();
            }
        });

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // This is the callback method that the system will invoke when your button is
//                // clicked. You might do this by launching another app or by including the
//                //functionality directly in this app.
//                // Hint: Use a Try/Catch block to handle the Intent dispatch gracefully, if you
//                // are using an external app.
//                //when you're done, remove the toast below.
//                Context context = getActivity();
//                CharSequence text = "This button should let you scan a book for its barcode!";
//                int duration = Toast.LENGTH_SHORT;
//
//                Toast toast = Toast.makeText(context, text, duration);
//                toast.show();

                Intent intent = new Intent(getActivity(), ScanActivity.class);
                getActivity().startActivityForResult(intent, REQUEST_CODE_SCAN);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ean.setText("");
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean.getText().toString());
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                ean.setText("");
            }
        });

        if(savedInstanceState!=null){
            ean.setText(savedInstanceState.getString(EAN_CONTENT));
            ean.setHint("");
        }

        return rootView;
    }

    private void restartLoader(){
        showProgressDialog();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(ean.getText().length()==0){
            return null;
        }
        String eanStr= ean.getText().toString();
        if(eanStr.length()==10 && !eanStr.startsWith("978")){
            eanStr="978"+eanStr;
        }
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        hideProgressDialog();

        if (!data.moveToFirst()) {
            clearFields();
            textViewEmpty.setVisibility(View.VISIBLE);

            return;
        }

        textViewEmpty.setVisibility(View.GONE);

        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        textViewBookTitle.setText(bookTitle);

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        textViewBookSubtitle.setText(bookSubTitle);

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String[] authorsArr = authors.split(",");
        textViewAuthors.setLines(authorsArr.length);
        textViewAuthors.setText(authors.replace(",", "\n"));
        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if(Patterns.WEB_URL.matcher(imgUrl).matches()){
            new DownloadImage(imageViewBookCover).execute(imgUrl);
            imageViewBookCover.setVisibility(View.VISIBLE);
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        textViewCategories.setText(categories);

        buttonSave.setVisibility(View.VISIBLE);
        buttonDelete.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private void clearFields(){
        textViewBookTitle.setText("");
        textViewBookSubtitle.setText("");
        textViewAuthors.setText("");
        textViewCategories.setText("");
        imageViewBookCover.setVisibility(View.INVISIBLE);
        buttonSave.setVisibility(View.INVISIBLE);
        buttonDelete.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
    }

    private void showProgressDialog() {
        if(progressDialog==null)
            progressDialog = new ProgressDialog(getActivity());

        progressDialog.setTitle("Loading..");
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog!=null && progressDialog.isShowing())
            progressDialog.hide();
    }
}
