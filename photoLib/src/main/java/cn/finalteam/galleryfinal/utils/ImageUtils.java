/*
 * Copyright (C) 2014 pengjianbo(pengjianbosoft@gmail.com), Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cn.finalteam.galleryfinal.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import cn.finalteam.galleryfinal.CoreConfig;

/**
 * Desction: Author:pengjianbo Date:15/12/7 下午7:32
 */
public class ImageUtils
{

	public static String getExtension(String paramString)
	{
		if (paramString == null)
			return null;
		int i = indexOfExtension(paramString);
		if (i == -1)
			return "";
		return paramString.substring(i + 1);
	}

	public static int indexOfExtension(String paramString)
	{
		if (paramString == null)
			return -1;
		int i = paramString.lastIndexOf('.');
		if (indexOfLastSeparator(paramString) > i)
			i = -1;
		return i;
	}

	public static int indexOfLastSeparator(String paramString)
	{
		if (paramString == null)
			return -1;
		return Math.max(paramString.lastIndexOf('/'),
				paramString.lastIndexOf('\\'));
	}

	public static String getFileName(String pathandname)
	{
		int start = pathandname.lastIndexOf("/");
		int end = pathandname.lastIndexOf(".");
		if (start != -1 && end != -1)
		{
			return pathandname.substring(start + 1, end);
		} else
		{
			return null;
		}
	}

	/**
	 * 保存Bitmap到文件
	 * 
	 * @param bitmap
	 * @param format
	 * @param target
	 */
	public static void saveBitmap(Bitmap bitmap, Bitmap.CompressFormat format,
			File target)
	{
		if (target.exists())
		{
			target.delete();
		}
		try
		{
			FileOutputStream out = new FileOutputStream(target);
			bitmap.compress(format, 100, out);
			out.flush();
			out.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param filePath
	 * @param url
	 * @return
	 */
	public static Bitmap compressImageFromFile(String filePath)
	{
		Bitmap bitmap = null;
		try
		{
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			newOpts.inJustDecodeBounds = true;// 只读边,不读内容
			newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
			bitmap = BitmapFactory.decodeFile(filePath, newOpts);
			newOpts.inJustDecodeBounds = false;
			int w = newOpts.outWidth;
			int h = newOpts.outHeight;
			float hh = 800f;// 设置高度为800f时，可以明显看到图片缩小了
			float ww = 480f;// 设置宽度为480f，可以明显看到图片缩小了
			int be = 1;// be=1表示不缩放
			if (w >= h && w > ww)
			{
				be = (int) ((newOpts.outWidth / ww) + 0.5);
			} else if (w < h && h > hh)
			{
				be = (int) ((newOpts.outHeight / hh) + 0.5);
			}
			if (be <= 0)
				be = 1;
			newOpts.inSampleSize = be;// 设置采样率

			newOpts.inPurgeable = true;// 同时设置才会有效
			newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收
			bitmap = BitmapFactory.decodeFile(filePath, newOpts);
			// 其实是无效的,大家尽管尝试
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return bitmap;
	}

	public static Bitmap rotateBitmap(String path, int orientation,
			int screenWidth, int screenHeight)
	{
		Bitmap bitmap = null;
		final int maxWidth = screenWidth / 2;
		final int maxHeight = screenHeight / 2;
		try
		{
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);
			int sourceWidth, sourceHeight;
			if (orientation == 90 || orientation == 270)
			{
				sourceWidth = options.outHeight;
				sourceHeight = options.outWidth;
			} else
			{
				sourceWidth = options.outWidth;
				sourceHeight = options.outHeight;
			}
			boolean compress = false;
			if (sourceWidth > maxWidth || sourceHeight > maxHeight)
			{
				float widthRatio = (float) sourceWidth / (float) maxWidth;
				float heightRatio = (float) sourceHeight / (float) maxHeight;

				options.inJustDecodeBounds = false;
				if (new File(path).length() > 512000)
				{
					float maxRatio = Math.max(widthRatio, heightRatio);
					options.inSampleSize = (int) maxRatio;
					compress = true;
				}
				bitmap = BitmapFactory.decodeFile(path, options);
			} else
			{
				bitmap = BitmapFactory.decodeFile(path);
			}
			if (orientation > 0)
			{
				Matrix matrix = new Matrix();
				// matrix.postScale(sourceWidth, sourceHeight);
				matrix.postRotate(orientation);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrix, true);
			}
			sourceWidth = bitmap.getWidth();
			sourceHeight = bitmap.getHeight();
			if ((sourceWidth > maxWidth || sourceHeight > maxHeight)
					&& compress)
			{
				float widthRatio = (float) sourceWidth / (float) maxWidth;
				float heightRatio = (float) sourceHeight / (float) maxHeight;
				float maxRatio = Math.max(widthRatio, heightRatio);
				sourceWidth = (int) ((float) sourceWidth / maxRatio);
				sourceHeight = (int) ((float) sourceHeight / maxRatio);
				Bitmap bm = Bitmap.createScaledBitmap(bitmap, sourceWidth,
						sourceHeight, true);
				bitmap.recycle();
				return bm;
			}
		} catch (Exception e)
		{
		}
		return bitmap;
	}

	/**
	 * 取某个范围的任意数
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static int getRandom(int min, int max)
	{
		Random random = new Random();
		int s = random.nextInt(max) % (max - min + 1) + min;
		return s;
	}

	// ADD
	/**
	 * 清理图片
	 */
	public static void clearImgCache()
	{
		new Thread(new Runnable() {
			@Override
			public void run()
			{
				File file = new File(CoreConfig.STORE_LOCAL_IMAGE_PATH);
				DataCleanManager.deleteFolderFile(file.getAbsolutePath(), false);
			}
		}).start();
	}

	/**
	 * 读取照片exif信息中的旋转角度
	 * 
	 * @param path
	 *            照片路径
	 * @return角度
	 */
	public static int readPictureDegree(String path)
	{
		int degree = 0;
		try
		{
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation)
			{
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 根据旋转的角度再次旋转回来
	 * 
	 * @param img
	 * @return
	 */
	public static Bitmap toturn(Bitmap img, float degree)
	{
		Matrix matrix = new Matrix();
		matrix.postRotate(degree); /* 翻转90度 */
		int width = img.getWidth();
		int height = img.getHeight();
		img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
		return img;
	}

}
