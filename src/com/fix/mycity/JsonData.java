package com.fix.mycity;

import com.google.gson.annotations.SerializedName;

public class JsonData {

	class ImgurResponse {
		@SerializedName("data")
		public ImgurData data;
	}

	class ImgurData {
		@SerializedName("id")
		public String data;
		@SerializedName("deletehash")
		public String deletehash;
		@SerializedName("link")
		public String link;
	}

}
