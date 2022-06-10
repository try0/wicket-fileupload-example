package jp.try0.wicket.example.fileupload;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileDescription;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.form.upload.FilesSelectedBehavior;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Bytes;


public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;

	private FeedbackPanel feedbackPabel;
	private Form<Void> fileForm;
	private FileUploadField fileUploadField;
	private Button btnUpload;

	public HomePage(final PageParameters parameters) {
		super(parameters);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		setOutputMarkupId(true);

		add(feedbackPabel = new FeedbackPanel("feedbacks") {
			{
				setOutputMarkupId(true);
				setOutputMarkupPlaceholderTag(true);
			}
		});
		add(fileForm = new Form<Void>("fileForm") {

			{
				setMultiPart(true);
				setFileMaxSize(Bytes.megabytes(10));

				add(fileUploadField = new FileUploadField("inputFile") {
					{
						// if use both #setRequired(true) and FilesSelectedBehavior,
						// FilesSelectedBehavior#onSelected is not raised.
						//　setRequired(true); 
						setOutputMarkupId(true);

						// for check sizes before upload
						add(new FilesSelectedBehavior() {

							@Override
							protected void onSelected(AjaxRequestTarget target,
									List<FileDescription> fileDescriptions) {
								System.out.println("FilesSelectedBehavior#onSelected");

								long maxFileBytes = getFileMaxSize().bytes();

								for (var description : fileDescriptions) {
									System.out.println("description.fileName: " + description.getFileName());

									if (description.getFileSize() > maxFileBytes) {
										error(fileForm.getString("uploadSingleFileTooLarge", Model.of(fileForm)));
										break;
									}
								}

								btnUpload.setEnabled(!hasError());
								target.add(feedbackPabel, btnUpload);
							}
						});
					}
				});

				add(btnUpload = new Button("btnUpload") {
					{
						setOutputMarkupId(true);
					}

					@Override
					public void onSubmit() {
						super.onSubmit();

						info(fileUploadField.getFileUpload().getClientFileName());
					}
				});
			}

		});
	}
}
