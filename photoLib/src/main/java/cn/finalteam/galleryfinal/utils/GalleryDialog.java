package cn.finalteam.galleryfinal.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ifcnt.photolib.R;


public class GalleryDialog extends Dialog
{
	private GalleryDialog	dialog	= null;

	public GalleryDialog(Context context)
	{
		super(context);
	}

	public GalleryDialog(Context context, int theme)
	{
		super(context, theme);
	}

	public GalleryDialog(final Activity actvity)
	{
		super(actvity);
		dialog = new GalleryDialog(actvity, R.style.dialog_style);
		dialog.setContentView(R.layout.gf_gallery_dialog);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);
		dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		LinearLayout ll_contant = (LinearLayout) dialog.findViewById(R.id.gallery_ll_contant);
		ImageView imageView = (ImageView) dialog.findViewById(R.id.gallery_loadingImageView);
		ll_contant.getBackground().setAlpha(70);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
		animationDrawable.start();
	}

	public void isCancelable(boolean isCancel)
	{
		dialog.setCancelable(isCancel);
	}

	/**
	 * 显示
	 */
	public void Show()
	{
		if (dialog != null && !dialog.isShowing())
		{
			dialog.show();
		}
	}

	/**
	 * 关闭
	 */
	public void Dimiss()
	{
		if (dialog != null && dialog.isShowing())
		{
			dialog.dismiss();
		}
	}

	/**
	 * [Summary] setTitile 标题
	 * @param strTitle
	 */
	public GalleryDialog setTitile(String strTitle)
	{
		return dialog;
	}

	/**
	 * [Summary] setMessage 提示内容
	 */
	public void setMessage(String strMessage)
	{
		TextView tvMsg = (TextView) dialog.findViewById(R.id.gallery_tv_loadingmsg);
		if (tvMsg != null)
		{
			tvMsg.setText(strMessage);
		}
	}

}