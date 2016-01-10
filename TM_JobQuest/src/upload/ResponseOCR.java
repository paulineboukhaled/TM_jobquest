package upload;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseOCR {
	// private String ErrorMessage;
	// private long AvailablePages;
	// private String OutputFileUrl;
	// private String OutputFileUrl2;
	// private String OutputFileUrl3;
	// private ArrayList<ArrayList<String>> OCRText;
	// private ArrayList<String> Reserved;
	// private ArrayList<String> OCRWords;
	// private String TaskDescription;
	//
	//
	// public ResponseOCR(String ErrorMessage, long AvailablePages,
	// ArrayList<ArrayList<String>> OCRText, String OutputFileUrl, String
	// OutputFileUrl2, String OutputFileUrl3, String TaskDescription,
	// ArrayList<String> Reserved,ArrayList<String> OCRWords){
	// setErrorMessage(ErrorMessage);
	// setAvailablePages(AvailablePages);
	// setOCRText(OCRText);
	// setOutputFileUrl(OutputFileUrl);
	// setOutputFileUrl(OutputFileUrl2);
	// setOutputFileUrl(OutputFileUrl3);
	// setTaskDescription(TaskDescription);
	// setReserved(Reserved);
	// setOCRText(OCRText);
	// }
	//
	//
	// public String getOutputFileUrl2() {
	// return OutputFileUrl2;
	// }
	//
	//
	// public void setOutputFileUrl2(String outputFileUrl2) {
	// OutputFileUrl2 = outputFileUrl2;
	// }
	//
	//
	// public String getOutputFileUrl3() {
	// return OutputFileUrl3;
	// }
	//
	//
	// public void setOutputFileUrl3(String outputFileUrl3) {
	// OutputFileUrl3 = outputFileUrl3;
	// }
	//
	//
	// public ArrayList<String> getOCRWords() {
	// return OCRWords;
	// }
	//
	//
	// public void setOCRWords(ArrayList<String> oCRWords) {
	// OCRWords = oCRWords;
	// }
	//
	//
	// public String getErrorMessage() {
	// return ErrorMessage;
	// }
	// public void setErrorMessage(String errorMessage) {
	// ErrorMessage = errorMessage;
	// }
	// public long getAvailablePages() {
	// return AvailablePages;
	// }
	// public void setAvailablePages(long availablePages) {
	// AvailablePages = availablePages;
	// }
	// public ArrayList<ArrayList<String>> getOCRText() {
	// return OCRText;
	// }
	// public void setOCRText(ArrayList<ArrayList<String>> oCRText) {
	// OCRText = oCRText;
	// }
	// public String getOutputFileUrl() {
	// return OutputFileUrl;
	// }
	// public void setOutputFileUrl(String outputFileUrl) {
	// OutputFileUrl = outputFileUrl;
	// }
	// public String getTaskDescription() {
	// return TaskDescription;
	// }
	// public void setTaskDescription(String taskDescription) {
	// TaskDescription = taskDescription;
	// }
	// public ArrayList<String> getReserved() {
	// return Reserved;
	// }
	// public void setReserved(ArrayList<String> reserved) {
	// Reserved = reserved;
	// }

	@SerializedName("ErrorMessage")
	@Expose
	private String ErrorMessage;
	@SerializedName("AvailablePages")
	@Expose
	private Integer AvailablePages;
	@SerializedName("OCRText")
	@Expose
	private List<List<String>> OCRText = new ArrayList<List<String>>();
	@SerializedName("OutputFileUrl")
	@Expose
	private String OutputFileUrl;
	@SerializedName("OutputFileUrl2")
	@Expose
	private String OutputFileUrl2;
	@SerializedName("OutputFileUrl3")
	@Expose
	private String OutputFileUrl3;
	@SerializedName("Reserved")
	@Expose
	private List<Object> Reserved = new ArrayList<Object>();
	@SerializedName("OCRWords")
	@Expose
	private List<Object> OCRWords = new ArrayList<Object>();
	@SerializedName("TaskDescription")
	@Expose
	private Object TaskDescription;

	/**
	 * 
	 * @return The ErrorMessage
	 */
	public String getErrorMessage() {
		return ErrorMessage;
	}

	/**
	 * 
	 * @param ErrorMessage
	 *            The ErrorMessage
	 */
	public void setErrorMessage(String ErrorMessage) {
		this.ErrorMessage = ErrorMessage;
	}

	/**
	 * 
	 * @return The AvailablePages
	 */
	public Integer getAvailablePages() {
		return AvailablePages;
	}

	/**
	 * 
	 * @param AvailablePages
	 *            The AvailablePages
	 */
	public void setAvailablePages(Integer AvailablePages) {
		this.AvailablePages = AvailablePages;
	}

	/**
	 * 
	 * @return The OCRText
	 */
	public List<List<String>> getOCRText() {
		return OCRText;
	}

	/**
	 * 
	 * @param OCRText
	 *            The OCRText
	 */
	public void setOCRText(List<List<String>> OCRText) {
		this.OCRText = OCRText;
	}

	/**
	 * 
	 * @return The OutputFileUrl
	 */
	public String getOutputFileUrl() {
		return OutputFileUrl;
	}

	/**
	 * 
	 * @param OutputFileUrl
	 *            The OutputFileUrl
	 */
	public void setOutputFileUrl(String OutputFileUrl) {
		this.OutputFileUrl = OutputFileUrl;
	}

	/**
	 * 
	 * @return The OutputFileUrl2
	 */
	public String getOutputFileUrl2() {
		return OutputFileUrl2;
	}

	/**
	 * 
	 * @param OutputFileUrl2
	 *            The OutputFileUrl2
	 */
	public void setOutputFileUrl2(String OutputFileUrl2) {
		this.OutputFileUrl2 = OutputFileUrl2;
	}

	/**
	 * 
	 * @return The OutputFileUrl3
	 */
	public String getOutputFileUrl3() {
		return OutputFileUrl3;
	}

	/**
	 * 
	 * @param OutputFileUrl3
	 *            The OutputFileUrl3
	 */
	public void setOutputFileUrl3(String OutputFileUrl3) {
		this.OutputFileUrl3 = OutputFileUrl3;
	}

	/**
	 * 
	 * @return The Reserved
	 */
	public List<Object> getReserved() {
		return Reserved;
	}

	/**
	 * 
	 * @param Reserved
	 *            The Reserved
	 */
	public void setReserved(List<Object> Reserved) {
		this.Reserved = Reserved;
	}

	/**
	 * 
	 * @return The OCRWords
	 */
	public List<Object> getOCRWords() {
		return OCRWords;
	}

	/**
	 * 
	 * @param OCRWords
	 *            The OCRWords
	 */
	public void setOCRWords(List<Object> OCRWords) {
		this.OCRWords = OCRWords;
	}

	/**
	 * 
	 * @return The TaskDescription
	 */
	public Object getTaskDescription() {
		return TaskDescription;
	}

	/**
	 * 
	 * @param TaskDescription
	 *            The TaskDescription
	 */
	public void setTaskDescription(Object TaskDescription) {
		this.TaskDescription = TaskDescription;
	}

}
