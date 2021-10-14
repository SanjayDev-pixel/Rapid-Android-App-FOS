package motobeans.architecture.customAppComponents.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.finance.app.R;

public class CommonDialogFragment extends DialogFragment {
    private static final String ARG_PARAM1 = "param1";
    private String msg;
    private View mView;
    private TextView okButton;
    private TextView cancelButton;

    public CommonDialogFragment() {
    }

    public static CommonDialogFragment newInstance(String param1) {
        CommonDialogFragment fragment = new CommonDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            msg = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
//        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.ALPHA_CHANGED);

        mView=inflater.inflate(R.layout.fragment_common_dialog, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setTitle("ALERT");
        TextView message= (TextView) mView.findViewById(R.id.message);

        message.setText(msg);

        okButton= (TextView) mView.findViewById(R.id.ok);
        cancelButton=(TextView) mView.findViewById(R.id.cancel);

        okButton.setOnClickListener(v->ok.onAction());
        cancelButton.setOnClickListener(v->cancel.onAction());

        okButton.setOnClickListener(v->ok.onAction());
        cancelButton.setOnClickListener(v->cancel.onAction());

        if(ok==null){
            okButton.setVisibility(View.GONE);
        }

        if(cancel==null){
            cancelButton.setVisibility(View.GONE);
        }

        if(ok==null && cancel==null){
            Toast.makeText(getContext(),"Please provdie listener",Toast.LENGTH_SHORT).show();
            dismiss();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if(getDialog()==null){
            return;
        }

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog,
                                 int keyCode, android.view.KeyEvent event) {
                if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                    // To dismiss the fragment when the back-button is pressed.
//                    dismiss();
                    return true;
                }
                // Otherwise, do nothing else
                else return false;
            }
        });
    }

    ICommonListener cancel;
    ICommonListener ok;
    public void setListener(ICommonListener cancel,ICommonListener ok){
        this.cancel=cancel;
        this.ok=ok;
    }

    public void setListener(ICommonListener single,boolean isCancel){
        if(isCancel){
            this.cancel=single;
        }else {
            this.ok=single;
        }
    }

    public interface ICommonListener{
        void onAction();
    }
}
