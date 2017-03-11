/*
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab and FG Language Technology
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tudarmstadt.ukp.clarin.webanno.ui.correction;

import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.getAddr;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.getNumberOfPages;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.getSentenceAddress;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.selectByAddr;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.selectSentenceAt;
import static org.apache.uima.fit.util.JCasUtil.selectFollowing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.wicketstuff.annotation.mount.MountPath;

import de.tudarmstadt.ukp.clarin.webanno.api.AnnotationService;
import de.tudarmstadt.ukp.clarin.webanno.api.RepositoryService;
import de.tudarmstadt.ukp.clarin.webanno.api.UserDao;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.exception.AnnotationException;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorState;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorStateImpl;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil;
import de.tudarmstadt.ukp.clarin.webanno.api.dao.SecurityUtil;
import de.tudarmstadt.ukp.clarin.webanno.brat.annotation.BratAnnotator;
import de.tudarmstadt.ukp.clarin.webanno.brat.util.BratAnnotatorUtility;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocument;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocumentStateTransition;
import de.tudarmstadt.ukp.clarin.webanno.model.Mode;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocumentState;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocumentStateTransition;
import de.tudarmstadt.ukp.clarin.webanno.model.User;
import de.tudarmstadt.ukp.clarin.webanno.support.dialog.ChallengeResponseDialog;
import de.tudarmstadt.ukp.clarin.webanno.support.dialog.ConfirmationDialog;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaAjaxLink;
import de.tudarmstadt.ukp.clarin.webanno.ui.annotation.PreferencesUtil;
import de.tudarmstadt.ukp.clarin.webanno.ui.annotation.component.AnnotationPreferencesModalPanel;
import de.tudarmstadt.ukp.clarin.webanno.ui.annotation.component.DocumentNamePanel;
import de.tudarmstadt.ukp.clarin.webanno.ui.annotation.component.ExportModalPanel;
import de.tudarmstadt.ukp.clarin.webanno.ui.annotation.component.FinishImage;
import de.tudarmstadt.ukp.clarin.webanno.ui.annotation.component.GuidelineModalPanel;
import de.tudarmstadt.ukp.clarin.webanno.ui.annotation.detail.AnnotationDetailEditorPanel;
import de.tudarmstadt.ukp.clarin.webanno.ui.annotation.dialog.OpenModalWindowPanel;
import de.tudarmstadt.ukp.clarin.webanno.ui.curation.component.SuggestionViewPanel;
import de.tudarmstadt.ukp.clarin.webanno.ui.curation.component.model.CurationContainer;
import de.tudarmstadt.ukp.clarin.webanno.ui.curation.component.model.CurationUserSegmentForAnnotationDocument;
import de.tudarmstadt.ukp.clarin.webanno.ui.curation.component.model.SourceListView;
import de.tudarmstadt.ukp.clarin.webanno.ui.curation.component.model.SuggestionBuilder;
import de.tudarmstadt.ukp.clarin.webanno.ui.curation.service.AnnotationSelection;
import de.tudarmstadt.ukp.clarin.webanno.ui.curation.service.CuratorUtil;
import de.tudarmstadt.ukp.clarin.webanno.webapp.core.app.ApplicationPageBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import wicket.contrib.input.events.EventType;
import wicket.contrib.input.events.InputBehavior;
import wicket.contrib.input.events.key.KeyType;

/**
 * This is the main class for the correction page. Displays in the lower panel the Automatically
 * annotated document and in the upper panel the corrected annotation
 *
 */
@MountPath("/correction.html")
public class CorrectionPage
    extends ApplicationPageBase
{
    private static final Logger LOG = LoggerFactory.getLogger(CorrectionPage.class);

    private static final long serialVersionUID = 1378872465851908515L;

    @SpringBean(name = "documentRepository")
    private RepositoryService repository;

    @SpringBean(name = "annotationService")
    private AnnotationService annotationService;

    @SpringBean(name = "userRepository")
    private UserDao userRepository;

    private CurationContainer curationContainer;

    private Label numberOfPages;
    private DocumentNamePanel documentNamePanel;
    private ModalWindow openDocumentsModal;
    
    private long currentprojectId;

    private NumberTextField<Integer> gotoPageTextField;
    private int gotoPageAddress;
    private AnnotationDetailEditorPanel editor;


    private SuggestionViewPanel correctionView;
    private BratAnnotator annotator;

    private Map<String, Map<Integer, AnnotationSelection>> annotationSelectionByUsernameAndAddress = new HashMap<String, Map<Integer, AnnotationSelection>>();

    private SourceListView curationSegment = new SourceListView();
    
    // Open the dialog window on first load
    private boolean firstLoad = true;
    
    private ChallengeResponseDialog resetDocumentDialog;
    private LambdaAjaxLink resetDocumentLink;
    
    private FinishImage finishDocumentIcon;
    private ConfirmationDialog finishDocumentDialog;
    private LambdaAjaxLink finishDocumentLink;
    
    public CorrectionPage()
    {
        setModel(Model.of(new AnnotatorStateImpl(Mode.CORRECTION)));

        WebMarkupContainer sidebarCell = new WebMarkupContainer("sidebarCell") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onComponentTag(ComponentTag aTag)
            {
                super.onComponentTag(aTag);
                AnnotatorState state = CorrectionPage.this.getModelObject();
                aTag.put("width", state.getPreferences().getSidebarSize()+"%");
            }
        };
        add(sidebarCell);

        WebMarkupContainer annotationViewCell = new WebMarkupContainer("annotationViewCell") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onComponentTag(ComponentTag aTag)
            {
                super.onComponentTag(aTag);
                AnnotatorState state = CorrectionPage.this.getModelObject();
                aTag.put("width", (100-state.getPreferences().getSidebarSize())+"%");
            }
        };
        add(annotationViewCell);
        
        LinkedList<CurationUserSegmentForAnnotationDocument> sentences = new LinkedList<CurationUserSegmentForAnnotationDocument>();
        CurationUserSegmentForAnnotationDocument curationUserSegmentForAnnotationDocument = new CurationUserSegmentForAnnotationDocument();
        if (getModelObject().getDocument() != null) {
            curationUserSegmentForAnnotationDocument
                    .setAnnotationSelectionByUsernameAndAddress(annotationSelectionByUsernameAndAddress);
            curationUserSegmentForAnnotationDocument.setBratAnnotatorModel(getModelObject());
            sentences.add(curationUserSegmentForAnnotationDocument);
        }
        correctionView = new SuggestionViewPanel("correctionView",
                new Model<LinkedList<CurationUserSegmentForAnnotationDocument>>(sentences))
        {
            private static final long serialVersionUID = 2583509126979792202L;

            @Override
            public void onChange(AjaxRequestTarget aTarget)
            {
                AnnotatorState state = CorrectionPage.this.getModelObject();
                
                aTarget.addChildren(getPage(), FeedbackPanel.class);
                try {
                    // update begin/end of the curationsegment based on bratAnnotatorModel changes
                    // (like sentence change in auto-scroll mode,....
                    curationContainer.setBratAnnotatorModel(state);
                    setCurationSegmentBeginEnd();

                    CuratorUtil.updatePanel(aTarget, this, curationContainer, annotator,
                            repository, annotationSelectionByUsernameAndAddress, curationSegment,
                            annotationService, userRepository);
                    
                    annotator.bratRenderLater(aTarget);
                    aTarget.add(numberOfPages);
                    update(repository.readCorrectionCas(state.getDocument()), aTarget);
                }
                catch (UIMAException e) {
                    LOG.error("Error: " + e.getMessage(), e);
                    error(ExceptionUtils.getRootCause(e));
                }
                catch (Exception e) {
                    LOG.error("Error: " + e.getMessage(), e);
                    error(e.getMessage());
                }
            }
        };

        correctionView.setOutputMarkupId(true);
        annotationViewCell.add(correctionView);

        editor = new AnnotationDetailEditorPanel("annotationDetailEditorPanel", getModel())
        {
            private static final long serialVersionUID = 2857345299480098279L;

            @Override
            protected void onChange(AjaxRequestTarget aTarget)
            {
                aTarget.addChildren(getPage(), FeedbackPanel.class);
                aTarget.add(correctionView);
                aTarget.add(numberOfPages);

                try {
                    AnnotatorState state = getModelObject();
                    JCas annotationCas = getEditorCas();
                    JCas correctionCas = repository.readCorrectionCas(state.getDocument());
                    annotator.bratRender(aTarget, annotationCas);
                    annotator.bratSetHighlight(aTarget, state.getSelection().getAnnotation());

                    // info(bratAnnotatorModel.getMessage());
                    SuggestionBuilder builder = new SuggestionBuilder(repository,
                            annotationService, userRepository);
                    curationContainer = builder.buildCurationContainer(state);
                    setCurationSegmentBeginEnd();
                    curationContainer.setBratAnnotatorModel(state);

                    CuratorUtil.updatePanel(aTarget, correctionView, curationContainer, annotator,
                            repository, annotationSelectionByUsernameAndAddress, curationSegment,
                            annotationService, userRepository);
                    
                    update(correctionCas, aTarget);
                }
                catch (UIMAException e) {
                    LOG.error("Error: " + e.getMessage(), e);
                    error(ExceptionUtils.getRootCause(e));
                }
                catch (Exception e) {
                    LOG.error("Error: " + e.getMessage(), e);
                    error(e.getMessage());
                }
            }

            @Override
            protected void onAutoForward(AjaxRequestTarget aTarget)
            {
                try {
                    annotator.bratRender(aTarget, getEditorCas());
                }
                catch (Exception e) {
                    LOG.error("Error reading CAS " + e.getMessage());
                    error("Error reading CAS " + e.getMessage());
                    return;
                }
            }
        };
        sidebarCell.add(editor);

        annotator = new BratAnnotator("mergeView", getModel(), editor);
        annotator.setOutputMarkupId(true);
        annotationViewCell.add(annotator);

        curationContainer = new CurationContainer();
        curationContainer.setBratAnnotatorModel(getModelObject());

        add(documentNamePanel = new DocumentNamePanel("documentNamePanel", getModel()));
        documentNamePanel.setOutputMarkupId(true);

        add(numberOfPages = (Label) new Label("numberOfPages",
                new LoadableDetachableModel<String>()
                {
                    private static final long serialVersionUID = 891566759811286173L;

                    @Override
                    protected String load()
                    {
                        AnnotatorState state = CorrectionPage.this.getModelObject();
                        
                        if (state.getDocument() != null) {

                            JCas mergeJCas = null;
                            try {
                                mergeJCas = repository.readCorrectionCas(state
                                        .getDocument());

                                int totalNumberOfSentence = getNumberOfPages(mergeJCas);

                                List<SourceDocument> listofDoc = getListOfDocs();
                            	
                                int docIndex = listofDoc.indexOf(state.getDocument()) + 1;
                            	
                                return "showing " + state.getFirstVisibleSentenceNumber() + "-"
                                        + state.getLastVisibleSentenceNumber() + " of "
                                        + totalNumberOfSentence + " sentences [document " + docIndex
                                        + " of " + listofDoc.size() + "]";
                            }
                            catch (Exception e) {
                                return "";
                            }
                        }
                        else {
                            return "";// no document yet selected
                        }

                    }
                }).setOutputMarkupId(true));

        add(openDocumentsModal = new ModalWindow("openDocumentsModal"));
        openDocumentsModal.setOutputMarkupId(true);
        openDocumentsModal.setInitialWidth(620);
        openDocumentsModal.setInitialHeight(440);
        openDocumentsModal.setResizable(true);
        openDocumentsModal.setWidthUnit("px");
        openDocumentsModal.setHeightUnit("px");
        openDocumentsModal.setTitle("Open document");

        add(new AnnotationPreferencesModalPanel("annotationLayersModalPanel", getModel(), editor)
        {
            private static final long serialVersionUID = -4657965743173979437L;

            @Override
            protected void onChange(AjaxRequestTarget aTarget)
            {
                actionCompletePreferencesChange(aTarget);
            }
        });

        add(new ExportModalPanel("exportModalPanel", getModel()){
            private static final long serialVersionUID = -468896211970839443L;

            {
                setOutputMarkupId(true);
                setOutputMarkupPlaceholderTag(true);
            }

            @Override
            protected void onConfigure()
            {
                super.onConfigure();
                AnnotatorState state = CorrectionPage.this.getModelObject();
                setVisible(state.getProject() != null
                        && (SecurityUtil.isAdmin(state.getProject(), repository, state.getUser())
                                || !state.getProject().isDisableExport()));
            }
        });

        gotoPageTextField = (NumberTextField<Integer>) new NumberTextField<Integer>("gotoPageText",
                new Model<Integer>(0));
        Form<Void> gotoPageTextFieldForm = new Form<Void>("gotoPageTextFieldForm");
        gotoPageTextFieldForm.add(new AjaxFormSubmitBehavior(gotoPageTextFieldForm, "submit")
        {
            private static final long serialVersionUID = -4549805321484461545L;

            @Override
            protected void onSubmit(AjaxRequestTarget aTarget)
            {
                actionEnterPageNumer(aTarget);
            }
        });

        gotoPageTextField.setType(Integer.class);
        gotoPageTextField.setMinimum(1);
        gotoPageTextField.setDefaultModelObject(1);
        add(gotoPageTextFieldForm.add(gotoPageTextField));
        gotoPageTextField.add(new AjaxFormComponentUpdatingBehavior("change")
        {
            private static final long serialVersionUID = -3853194405966729661L;

            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
                AnnotatorState state = CorrectionPage.this.getModelObject();
                JCas mergeJCas = null;
                try {
                    mergeJCas = repository.readCorrectionCas(state.getDocument());
                    gotoPageAddress = getSentenceAddress(mergeJCas,
                            gotoPageTextField.getModelObject());
                }
                catch (UIMAException e) {
                    LOG.error("Error: " + e.getMessage(), e);
                    error(ExceptionUtils.getRootCause(e));
                }
                catch (Exception e) {
                    LOG.error("Error: " + e.getMessage(), e);
                    error(e.getMessage());
                }

            }
        });

        add(new LambdaAjaxLink("showOpenDocumentModal", this::actionOpenDocument));

        add(new LambdaAjaxLink("showPreviousDocument", this::actionShowPreviousDocument)
                .add(new InputBehavior(new KeyType[] { KeyType.Shift, KeyType.Page_up },
                        EventType.click)));

        add(new LambdaAjaxLink("showNextDocument", this::actionShowNextDocument)
                .add(new InputBehavior(new KeyType[] { KeyType.Shift, KeyType.Page_down },
                        EventType.click)));

        add(new LambdaAjaxLink("showNext", this::actionShowNextPage)
                .add(new InputBehavior(new KeyType[] { KeyType.Page_down }, EventType.click)));

        add(new LambdaAjaxLink("showPrevious", this::actionShowPreviousPage)
                .add(new InputBehavior(new KeyType[] { KeyType.Page_up }, EventType.click)));

        add(new LambdaAjaxLink("showFirst", this::actionShowFirstPage)
                .add(new InputBehavior(new KeyType[] { KeyType.Home }, EventType.click)));

        add(new LambdaAjaxLink("showLast", this::actionShowLastPage)
                .add(new InputBehavior(new KeyType[] { KeyType.End }, EventType.click)));

        add(new LambdaAjaxLink("gotoPageLink", this::actionGotoPage));

        add(new LambdaAjaxLink("toggleScriptDirection", this::actionToggleScriptDirection));
        
        add(new GuidelineModalPanel("guidelineModalPanel", getModel()));
        
        IModel<String> documentNameModel = PropertyModel.of(getModel(), "document.name");
        add(resetDocumentDialog = new ChallengeResponseDialog("resetDocumentDialog",
                new StringResourceModel("ResetDocumentDialog.title", this, null),
                new StringResourceModel("ResetDocumentDialog.text", this, getModel(),
                        documentNameModel),
                documentNameModel));
        add(resetDocumentLink = new LambdaAjaxLink("showResetDocumentDialog",
                this::actionResetDocument)
        {
            private static final long serialVersionUID = 874573384012299998L;

            @Override
            protected void onConfigure()
            {
                super.onConfigure();
                AnnotatorState state = CorrectionPage.this.getModelObject();
                setEnabled(state.getDocument() != null
                        && !repository.isAnnotationFinished(state.getDocument(), state.getUser()));
            }
        });
        resetDocumentLink.setOutputMarkupId(true);
        
        add(finishDocumentDialog = new ConfirmationDialog("finishDocumentDialog",
                new StringResourceModel("FinishDocumentDialog.title", this, null),
                new StringResourceModel("FinishDocumentDialog.text", this, null)));
        add(finishDocumentLink = new LambdaAjaxLink("showFinishDocumentDialog",
                this::actionFinishDocument)
        {
            private static final long serialVersionUID = 874573384012299998L;

            @Override
            protected void onConfigure()
            {
                super.onConfigure();
                AnnotatorState state = CorrectionPage.this.getModelObject();
                setEnabled(state.getDocument() != null
                        && !repository.isAnnotationFinished(state.getDocument(), state.getUser()));
            }
        });
        finishDocumentIcon = new FinishImage("finishImage", getModel());
        finishDocumentIcon.setOutputMarkupId(true);
        finishDocumentLink.add(finishDocumentIcon);
    }
    
    public void setModel(IModel<AnnotatorState> aModel)
    {
        setDefaultModel(aModel);
    }
    
    @SuppressWarnings("unchecked")
    public IModel<AnnotatorState> getModel()
    {
        return (IModel<AnnotatorState>) getDefaultModel();
    }

    public void setModelObject(AnnotatorState aModel)
    {
        setDefaultModelObject(aModel);
    }
    
    public AnnotatorState getModelObject()
    {
        return (AnnotatorState) getDefaultModelObject();
    }
    
    private List<SourceDocument> getListOfDocs()
    {
        AnnotatorState state = getModelObject();
        return new ArrayList<>(
                repository.listAnnotatableDocuments(state.getProject(), state.getUser()).keySet());
    }

    /**
     * for the first time the page is accessed, open the <b>open document dialog</b>
     */
    @Override
    public void renderHead(IHeaderResponse response)
    {
        super.renderHead(response);

        if (firstLoad) {
            response.render(OnLoadHeaderItem
                    .forScript("jQuery('#showOpenDocumentModal').trigger('click');"));
            firstLoad = false;
        }
    }
    
    private JCas getEditorCas()
        throws IOException, UIMAException, ClassNotFoundException
    {
        AnnotatorState state = getModelObject();

        if (state.getDocument() == null) {
            throw new IllegalStateException("Please open a document first!");
        }

        return repository.readCorrectionCas(state.getDocument());
    }
    
    private void setCurationSegmentBeginEnd()
        throws UIMAException, ClassNotFoundException, IOException
    {
        AnnotatorState state = getModelObject();
        
        JCas jCas = repository.readAnnotationCas(state.getDocument(), state.getUser());

        final int sentenceAddress = getAddr(selectSentenceAt(jCas,
                state.getFirstVisibleSentenceBegin(), state.getFirstVisibleSentenceEnd()));

        final Sentence sentence = selectByAddr(jCas, Sentence.class, sentenceAddress);
        List<Sentence> followingSentences = selectFollowing(jCas, Sentence.class, sentence,
                state.getPreferences().getWindowSize());
        // Check also, when getting the last sentence address in the display window, if this is the
        // last sentence or the ONLY sentence in the document
        Sentence lastSentenceAddressInDisplayWindow = followingSentences.size() == 0 ? sentence
                : followingSentences.get(followingSentences.size() - 1);
        curationSegment.setBegin(sentence.getBegin());
        curationSegment.setEnd(lastSentenceAddressInDisplayWindow.getEnd());
    }

    private void updateSentenceAddress(JCas aJCas, AjaxRequestTarget aTarget)
        throws UIMAException, IOException, ClassNotFoundException
    {
        AnnotatorState state = getModelObject();

        gotoPageAddress = WebAnnoCasUtil.getSentenceAddress(aJCas,
                gotoPageTextField.getModelObject());

        String labelText = "";
        if (state.getDocument() != null) {

            List<SourceDocument> listofDoc = getListOfDocs();

            int docIndex = listofDoc.indexOf(state.getDocument()) + 1;

            int totalNumberOfSentence = WebAnnoCasUtil.getNumberOfPages(aJCas);

            // If only one page, start displaying from sentence 1
            if (totalNumberOfSentence == 1) {
                state.setFirstVisibleSentence(WebAnnoCasUtil.getFirstSentence(aJCas));
            }

            labelText = "showing " + state.getFirstVisibleSentenceNumber() + "-"
                    + state.getLastVisibleSentenceNumber() + " of " + totalNumberOfSentence
                    + " sentences [document " + docIndex + " of " + listofDoc.size() + "]";
        }
        else {
            labelText = "";// no document yet selected
        }

        numberOfPages.setDefaultModelObject(labelText);
        aTarget.add(numberOfPages);
        aTarget.add(gotoPageTextField);
    }
    
    private void update(JCas aJCas, AjaxRequestTarget target)
        throws UIMAException, ClassNotFoundException, IOException, AnnotationException
    {
        CuratorUtil.updatePanel(target, correctionView, curationContainer, annotator, repository,
                annotationSelectionByUsernameAndAddress, curationSegment, annotationService,
                userRepository);

        gotoPageTextField.setModelObject(getModelObject().getFirstVisibleSentenceNumber());
        gotoPageAddress = getSentenceAddress(aJCas, gotoPageTextField.getModelObject());

        target.add(gotoPageTextField);
        target.add(correctionView);
        target.add(numberOfPages);
    }
    
    private void actionOpenDocument(AjaxRequestTarget aTarget)
    {
        AnnotatorState state = getModelObject();
        state.getSelection().clear();
        openDocumentsModal.setContent(new OpenModalWindowPanel(openDocumentsModal.getContentId(),
                state, openDocumentsModal, state.getMode()));
        openDocumentsModal.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
        {
            private static final long serialVersionUID = -1746088901018629567L;

            @Override
            public void onClose(AjaxRequestTarget target)
            {
                if (state.getDocument() == null) {
                    setResponsePage(getApplication().getHomePage());
                    return;
                }

                target.addChildren(getPage(), FeedbackPanel.class);
                try {
                    state.setDocument(state.getDocument(), getListOfDocs());
                    state.setProject(state.getProject());

                    actionLoadDocument(target);
                    setCurationSegmentBeginEnd();
                    update(repository.readCorrectionCas(state.getDocument()), target);

                    String username = SecurityContextHolder.getContext().getAuthentication()
                            .getName();
                    User user = userRepository.get(username);
                    editor.setEnabled(!FinishImage.isFinished(getModel(), user, repository));
                    editor.loadFeatureEditorModels(target);
                }
                catch (Exception e) {
                    LOG.error("Unable to load data", e);
                    error("Unable to load data: " + ExceptionUtils.getRootCauseMessage(e));
                }
                target.add(finishDocumentIcon);
                target.appendJavaScript(
                        "Wicket.Window.unloadConfirmation=false;window.location.reload()");
                target.add(documentNamePanel);
                target.add(numberOfPages);
            }
        });
        openDocumentsModal.show(aTarget);
    }

    /**
     * Show the previous document, if exist
     */
    private void actionShowPreviousDocument(AjaxRequestTarget aTarget)
    {
        getModelObject().moveToPreviousDocument(getListOfDocs());
        actionLoadDocument(aTarget);
    }

    /**
     * Show the next document if exist
     */
    private void actionShowNextDocument(AjaxRequestTarget aTarget)
    {
        getModelObject().moveToNextDocument(getListOfDocs());
        actionLoadDocument(aTarget);
    }

    private void actionGotoPage(AjaxRequestTarget aTarget)
    {
        AnnotatorState state = getModelObject();
        
        if (gotoPageAddress == 0) {
            aTarget.appendJavaScript("alert('The sentence number entered is not valid')");
            return;
        }
        if (state.getDocument() == null) {
            aTarget.appendJavaScript("alert('Please open a document first!')");
            return;
        }
        JCas correctionCas = null;
        try {
            aTarget.addChildren(getPage(), FeedbackPanel.class);
            correctionCas = repository.readCorrectionCas(state.getDocument());
            if (state.getFirstVisibleSentenceAddress() != gotoPageAddress) {
                Sentence sentence = selectByAddr(correctionCas, Sentence.class, gotoPageAddress);
                state.setFirstVisibleSentence(sentence);

                SuggestionBuilder builder = new SuggestionBuilder(repository, annotationService,
                        userRepository);
                curationContainer = builder.buildCurationContainer(state);
                setCurationSegmentBeginEnd();
                curationContainer.setBratAnnotatorModel(state);
                update(correctionCas, aTarget);
                annotator.bratRenderLater(aTarget);
            }
        }
        catch (UIMAException e) {
            LOG.error("Error: " + e.getMessage(), e);
            error(ExceptionUtils.getRootCause(e));
        }
        catch (Exception e) {
            LOG.error("Error: " + e.getMessage(), e);
            error(e.getMessage());
        }
    }

    private void actionEnterPageNumer(AjaxRequestTarget aTarget)
    {
        if (gotoPageAddress == 0) {
            aTarget.appendJavaScript("alert('The sentence number entered is not valid')");
            return;
        }
        
        AnnotatorState state = CorrectionPage.this.getModelObject();
        
        aTarget.addChildren(getPage(), FeedbackPanel.class);
        JCas correctionCas = null;
        try {
            correctionCas = repository.readCorrectionCas(state.getDocument());
            if (state.getFirstVisibleSentenceAddress() != gotoPageAddress) {
                Sentence sentence = selectByAddr(correctionCas, Sentence.class, gotoPageAddress);
                state.setFirstVisibleSentence(sentence);
    
                SuggestionBuilder builder = new SuggestionBuilder(repository,
                        annotationService, userRepository);
                curationContainer = builder.buildCurationContainer(state);
                setCurationSegmentBeginEnd();
                curationContainer.setBratAnnotatorModel(state);
                update(correctionCas, aTarget);
                annotator.bratRenderLater(aTarget);
            }
        }
        catch (UIMAException e) {
            LOG.error("Error: " + e.getMessage(), e);
            error(ExceptionUtils.getRootCause(e));
        }
        catch (Exception e) {
            LOG.error("Error: " + e.getMessage(), e);
            error(e.getMessage());
        }
    }

    private void actionShowPreviousPage(AjaxRequestTarget aTarget)
        throws UIMAException, ClassNotFoundException, IOException, AnnotationException
    {
        JCas jcas = getEditorCas();
        getModelObject().moveToPreviousPage(jcas);
        actionRefreshDocument(aTarget, jcas);
    }

    private void actionShowNextPage(AjaxRequestTarget aTarget)
        throws UIMAException, ClassNotFoundException, IOException, AnnotationException
    {
        JCas jcas = getEditorCas();
        getModelObject().moveToNextPage(jcas);
        actionRefreshDocument(aTarget, jcas);
    }

    private void actionShowFirstPage(AjaxRequestTarget aTarget)
        throws UIMAException, ClassNotFoundException, IOException, AnnotationException
    {
        JCas jcas = getEditorCas();
        getModelObject().moveToFirstPage(jcas);
        actionRefreshDocument(aTarget, jcas);
    }

    private void actionShowLastPage(AjaxRequestTarget aTarget)
        throws UIMAException, ClassNotFoundException, IOException, AnnotationException
    {
        JCas jcas = getEditorCas();
        getModelObject().moveToLastPage(jcas);
        actionRefreshDocument(aTarget, jcas);
    }

    private void actionToggleScriptDirection(AjaxRequestTarget aTarget)
    {
        AnnotatorState state = getModelObject();
        state.toggleScriptDirection();

        try {
            curationContainer.setBratAnnotatorModel(state);
            CuratorUtil.updatePanel(aTarget, correctionView, curationContainer, annotator,
                    repository, annotationSelectionByUsernameAndAddress, curationSegment,
                    annotationService, userRepository);
        }
        catch (Exception e) {
            error("Error: " + e.getMessage());
            LOG.error("Error: {}", e.getMessage(), e);
        }

        annotator.bratRenderLater(aTarget);
    }
    
    private void actionCompletePreferencesChange(AjaxRequestTarget aTarget)
    {
        AnnotatorState state = CorrectionPage.this.getModelObject();
        
        // Re-render the whole page because the width of the sidebar may have changed
        aTarget.add(CorrectionPage.this);
        
        curationContainer.setBratAnnotatorModel(state);
        try {
            setCurationSegmentBeginEnd();
            update(repository.readCorrectionCas(state.getDocument()), aTarget);
            // mergeVisualizer.reloadContent(aTarget);
            aTarget.appendJavaScript(
                    "Wicket.Window.unloadConfirmation = false;window.location.reload()");
        }
        catch (UIMAException e) {
            LOG.error("Error: " + e.getMessage(), e);
            error(ExceptionUtils.getRootCauseMessage(e));
        }
        catch (Exception e) {
            LOG.error("Error: " + e.getMessage(), e);
            error(e.getMessage());
        }    }

    private void actionResetDocument(AjaxRequestTarget aTarget)
    {
        resetDocumentDialog.setConfirmAction((target) -> {
            AnnotatorState state = getModelObject();
            JCas jcas = repository.createOrReadInitialCas(state.getDocument());
            repository.writeAnnotationCas(jcas, state.getDocument(), state.getUser());
            actionLoadDocument(target);
        });
        resetDocumentDialog.show(aTarget);
    }
    
    private void actionFinishDocument(AjaxRequestTarget aTarget)
    {
        finishDocumentDialog.setConfirmAction((target) -> {
            AnnotatorState state = getModelObject();
            AnnotationDocument annotationDocument = repository.getAnnotationDocument(
                    state.getDocument(), state.getUser());

            annotationDocument.setState(AnnotationDocumentStateTransition.transition(
                    AnnotationDocumentStateTransition.ANNOTATION_IN_PROGRESS_TO_ANNOTATION_FINISHED));
            
            // manually update state change!! No idea why it is not updated in the DB
            // without calling createAnnotationDocument(...)
            repository.createAnnotationDocument(annotationDocument);
            
            target.add(finishDocumentIcon);
            target.add(finishDocumentLink);
            target.add(editor);
            target.add(resetDocumentLink);
        });
        finishDocumentDialog.show(aTarget);
    }

    private void actionLoadDocument(AjaxRequestTarget aTarget)
    {
        LOG.info("BEGIN LOAD_DOCUMENT_ACTION");

        AnnotatorState state = getModelObject();
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.get(username);

        state.setUser(user);

        try {
            // Check if there is an annotation document entry in the database. If there is none,
            // create one.
            AnnotationDocument annotationDocument = repository
                    .createOrGetAnnotationDocument(state.getDocument(), user);

            // Read the correction CAS - if it does not exist yet, from the initial CAS
            JCas correctionCas;
            if (repository.existsCorrectionCas(state.getDocument())) {
                correctionCas = repository.readCorrectionCas(state.getDocument());
            }
            else {
                correctionCas = repository.createOrReadInitialCas(state.getDocument());
            }

            // Read the annotation CAS or create an annotation CAS from the initial CAS by stripping
            // annotations
            JCas annotationCas;
            if (repository.existsCas(state.getDocument(), user.getUsername())) {
                annotationCas = repository.readAnnotationCas(annotationDocument);
            }
            else {
                annotationCas = repository.createOrReadInitialCas(state.getDocument());
                annotationCas = BratAnnotatorUtility.clearJcasAnnotations(annotationCas,
                        state.getDocument(), user, repository);
            }

            // Update the CASes
            repository.upgradeCas(annotationCas.getCas(), annotationDocument);
            repository.upgradeCorrectionCas(correctionCas.getCas(), state.getDocument());

            // After creating an new CAS or upgrading the CAS, we need to save it
            repository.writeAnnotationCas(annotationCas.getCas().getJCas(),
                    annotationDocument.getDocument(), user);
            repository.writeCorrectionCas(correctionCas, state.getDocument(), user);

            // (Re)initialize brat model after potential creating / upgrading CAS
            state.clearAllSelections();

            // Load constraints
            state.setConstraints(repository.loadConstraints(state.getProject()));

            // Load user preferences
            PreferencesUtil.loadPreferences(username, repository, annotationService, state,
                    state.getMode());

            // Initialize the visible content
            state.setFirstVisibleSentence(WebAnnoCasUtil.getFirstSentence(annotationCas));
            
            // if project is changed, reset some project specific settings
            if (currentprojectId != state.getProject().getId()) {
                state.clearRememberedFeatures();
            }

            currentprojectId = state.getProject().getId();

            LOG.debug("Configured BratAnnotatorModel for user [" + state.getUser() + "] f:["
                    + state.getFirstVisibleSentenceNumber() + "] l:["
                    + state.getLastVisibleSentenceNumber() + "] s:["
                    + state.getFocusSentenceNumber() + "]");

            gotoPageTextField.setModelObject(1);

            setCurationSegmentBeginEnd();
            updateSentenceAddress(correctionCas, aTarget);
            update(correctionCas, aTarget);

            // Re-render the whole page because the font size
            aTarget.add(CorrectionPage.this);

            // Update document state
            if (state.getDocument().getState().equals(SourceDocumentState.NEW)) {
                state.getDocument().setState(SourceDocumentStateTransition
                        .transition(SourceDocumentStateTransition.NEW_TO_ANNOTATION_IN_PROGRESS));
                repository.createSourceDocument(state.getDocument());
            }
            
            editor.reset(aTarget);
        }
        catch (UIMAException e) {
            LOG.error("Error", e);
            aTarget.addChildren(getPage(), FeedbackPanel.class);
            error(ExceptionUtils.getRootCauseMessage(e));
        }
        catch (Exception e) {
            LOG.error("Error", e);
            aTarget.addChildren(getPage(), FeedbackPanel.class);
            error("Error: " + e.getMessage());
        }

        LOG.debug("Configured BratAnnotatorModel for user [" + state.getUser() + "] f:["
                + state.getFirstVisibleSentenceNumber() + "] l:["
                + state.getLastVisibleSentenceNumber() + "] s:[" + state.getFocusSentenceNumber()
                + "]");

        LOG.info("END LOAD_DOCUMENT_ACTION");
    }

    private void actionRefreshDocument(AjaxRequestTarget aTarget, JCas aJCas)
        throws UIMAException, ClassNotFoundException, IOException, AnnotationException
    {
        AnnotatorState state = getModelObject();
        SuggestionBuilder builder = new SuggestionBuilder(repository, annotationService,
                userRepository);
        curationContainer = builder.buildCurationContainer(state);
        setCurationSegmentBeginEnd();
        curationContainer.setBratAnnotatorModel(state);
        update(aJCas, aTarget);
        annotator.bratRenderLater(aTarget);
    }
}