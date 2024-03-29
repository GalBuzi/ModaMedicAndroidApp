package View;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.example.modamedicandroidapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Controller.AppController;
import Model.Questionnaires.Answer;
import Model.Questionnaires.Question;
import Model.Questionnaires.Questionnaire;
import Model.Utils.NetworkUtils;
import View.ViewUtils.BindingValues;
import View.ViewUtils.MessageUtils;

public class QuestionnaireActivity extends AbstractActivity {

    private static final String TAG = "QuestionnaireActivity";
    Questionnaire questionnaire;
    long currentQuestionID;
    Map<Long, List<Long>> questionsAnswers; //key: questionID, value: list of answerID
    Map<Long, Map<Long, Button>> answersButtons; //key:: answerID, value: answer Button
    private SeekBar answerEQ5SeekBar = null;
    private boolean eq5Answered = false;
    private TextView eq5result = null;
    private ImageView eq5face = null;
    private Map<Long, String> medicineInfo = null;
    private Map<Long, Integer> VAS_Colors = null;
    private float answersTextSize;
    private float questionTextSize;
    private float descriptionTextSize;
    private long answerVasTextSize;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        if (!NetworkUtils.hasInternetConnection(QuestionnaireActivity.this)) {
            MessageUtils.showAlert(QuestionnaireActivity.this,getString(R.string.no_internet_connection));
            return;
        }
        questionsAnswers = new HashMap<>();
        answersButtons = new HashMap<>();
        answersTextSize = 15;
        answerVasTextSize = 20;
        questionTextSize = 20;
        descriptionTextSize = 15;
        Intent intent = getIntent();
        long questionnaire_id = 0;
        setLocationOfPlusMinusButtons(false);
        questionnaire = (Questionnaire) intent.getSerializableExtra(BindingValues.REQUESTED_QUESTIONNAIRE);
        category = intent.getStringExtra(BindingValues.REQUESTED_QUESTIONNAIRE_CATEGORY);
        showTitle();
        showQuestion(0);

    }

    private void setLocationOfPlusMinusButtons(boolean isEQ5) {
        FloatingActionButton plus = findViewById(R.id.textPlus);
        FloatingActionButton minus = findViewById(R.id.textMinus);
        RelativeLayout.LayoutParams params = new RelativeLayout .LayoutParams(
                RelativeLayout .LayoutParams.WRAP_CONTENT, RelativeLayout .LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams params2 = new RelativeLayout .LayoutParams(
                RelativeLayout .LayoutParams.WRAP_CONTENT, RelativeLayout .LayoutParams.WRAP_CONTENT);
        int height = getHeightOfScreen();
        if (!isEQ5) {
            params.setMargins(10, (int) (height/2 - 0.06*height), 0, 0);
            params2.setMargins(10,(int) (height/2 + 0.06*height), 0, 0);
        }
        else {
            params.setMargins(10, (int) (4*height/5 - 0.06*height), 0, 0);
            params2.setMargins(10, (int) (4*height/5 + 0.06*height), 0,  0);
        }
        params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        plus.setLayoutParams(params);
        minus.setLayoutParams(params2);
    }

    private void showTitle() {
        TextView title = findViewById(R.id.questionnaire_title);
        title.setText(this.getString(R.string.questionnaire) + " " + questionnaire.getTitle());
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP,questionTextSize);
        title.setBackground(getDrawable(R.drawable.custom_chosen_button));

    }

    private void showQuestion(final long ii) {
        final int i = safeLongToInt(ii);
        currentQuestionID = questionnaire.getQuestions().get(i).getQuestionID();
        answersButtons.put(currentQuestionID, new HashMap<Long, Button>());
        final LinearLayout layout = findViewById(R.id.lin_layout);
        String ques_TEXT = questionnaire.getQuestions().get(i).getQuestionText();
        TextView question_TV = new TextView(this);
        question_TV.setText(ques_TEXT);
        question_TV.setTextSize(TypedValue.COMPLEX_UNIT_SP,questionTextSize);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        question_TV.setGravity(Gravity.CENTER);
        question_TV.setLayoutParams(params);
        layout.addView(question_TV);
        Question.Type type = questionnaire.getQuestions().get(i).getType();
        if (type.equals(Question.Type.SINGLE)) {
            TextView twoWeeksTV = BuildTwoWeeks();
            layout.addView(twoWeeksTV);
        }
        BuildQuestionByType(type, i);

        //next previous buttons
        FloatingActionButton nextButton = findViewById(R.id.nextButton);
        //setLocationOfButtonInRelativeLayout(nextButton,"next");

        FloatingActionButton prevButton = findViewById(R.id.prevButton);
        //setLocationOfButtonInRelativeLayout(prevButton,"previous");

        if (i < questionnaire.getQuestions().size() - 1) { // not last question


            setNextButtonActionForAllQuestionsExceptLast(ii, layout, nextButton);

            if (i > 0) { //not first question
                setPreviousButtonActionForAllQuestionsExceptFirst(layout, prevButton);
            } else { //first question
                setInvisible(prevButton);
            }
        } else { //last question in questionnaire
            //nextButton.setImageResource(android.R.drawable.ic_menu_send);
            setNextButtonActionForLastQuestion(ii, layout, nextButton);
            if (i == 0) //first question
                setInvisible(prevButton);
            else
                setPreviousButtonActionForAllQuestionsExceptFirst(layout, prevButton);

        }


    }

    private TextView BuildTwoWeeks() {
        String two_weeks = getString(R.string.consider_last_time);
        TextView twoWeeksTV = new TextView(this);
        twoWeeksTV.setText(two_weeks);
        twoWeeksTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,descriptionTextSize);
        LinearLayout.LayoutParams two_weeks_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        two_weeks_params.setMargins(10, 10, 10, 10);
        twoWeeksTV.setGravity(Gravity.CENTER);
        twoWeeksTV.setLayoutParams(two_weeks_params);
        return twoWeeksTV;
    }

    @SuppressLint("RestrictedApi")
    private void setInvisible(FloatingActionButton prevButton) {
        prevButton.setVisibility(View.INVISIBLE);
    }

    private void setNextButtonActionForLastQuestion(long ii, LinearLayout layout, FloatingActionButton nextButton) {
        nextButton.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                boolean isEq5 = questionnaire.getQuestionaireID() == 6 && answerEQ5SeekBar != null;
                if (isEq5) {
                    //special section for EQ5 special question
                    String answerNumber = String.valueOf(answerEQ5SeekBar.getProgress());
                    if (!eq5Answered) {
                        Toast.makeText(v.getContext(), R.string.answerTheQuestion, Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        eq5Answered = false;
                        List<Long> answer = new ArrayList<>();
                        answer.add(Long.parseLong(answerNumber));
                        questionsAnswers.put(Long.parseLong("0"), answer);
                    }
                }

                if (!isEq5 && (questionsAnswers.get(ii) == null || questionsAnswers.get(ii).isEmpty())) {
                    Toast.makeText(v.getContext(), R.string.answerTheQuestion, Toast.LENGTH_SHORT).show();
                } else {
                    sendAnswersToServer();
                    layout.removeAllViews();
                    if (questionnaire.getQuestionaireID() == 5) {
                        long six = 6;
                        openQuestionnaireActivity("EQ-5 Special Question", six);
                    } else {
                        FloatingActionButton nextButton = findViewById(R.id.nextButton);
                        animateFullCircle(nextButton);
                        nextButton.setVisibility(View.INVISIBLE);
                        FloatingActionButton prevButton = findViewById(R.id.prevButton);
                        prevButton.setVisibility(View.INVISIBLE);
                        TextView thanksTV = new TextView(v.getContext());
                        thanksTV.setText(R.string.thanks);
                        thanksTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,questionTextSize);
                        thanksTV.setGravity(Gravity.CENTER);
                        layout.addView(thanksTV);
                        TextView sentTV = new TextView(v.getContext());
                        sentTV.setText(R.string.sent_succesfully);
                        sentTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,questionTextSize);
                        sentTV.setGravity(Gravity.CENTER);
                        layout.addView(sentTV);
                        Button backButton = new Button(v.getContext());
                        backButton.setText(R.string.back_to_home_page);
                        backButton.setWidth(20);
                        backButton.setHeight(10);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0,100,0,0);

                        FloatingActionButton plus = findViewById(R.id.textPlus);
                        plus.setVisibility(View.INVISIBLE);
                        FloatingActionButton minus = findViewById(R.id.textMinus);
                        minus.setVisibility(View.INVISIBLE);

                        backButton.setLayoutParams(params);
                        backButton.setGravity(Gravity.CENTER);
                        backButton.setBackground(getDrawable(R.drawable.custom_system_button));
                        backButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                backToHomePage(v);
                            }
                        });
                        layout.addView(backButton);
                    }
                }

            }
        });
    }

    private void backToHomePage(View v) {
        finish();
        Intent intent = new Intent(this, HomePageBodyActivity.class);
        startActivity(intent);
    }

    private void animateFullCircle(FloatingActionButton button) {
        float deg = button.getRotation() + 360F;
        button.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
    }

    private void setNextButtonActionForAllQuestionsExceptLast(long ii, LinearLayout layout, FloatingActionButton nextButton) {
        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (questionsAnswers.get(ii) == null || questionsAnswers.get(ii).isEmpty()) {
                    Toast.makeText(v.getContext(), R.string.answerTheQuestion, Toast.LENGTH_SHORT).show();
                } else {
                    layout.removeAllViews();
                    showQuestion(++currentQuestionID);
                    animateFullCircle(nextButton);
                }

            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void setPreviousButtonActionForAllQuestionsExceptFirst(LinearLayout layout, FloatingActionButton prevButton) {
        prevButton.setVisibility(View.VISIBLE);
        prevButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), R.string.movingToPrevQuestion, Toast.LENGTH_SHORT).show();
                animateFullCircle(prevButton);
                layout.removeAllViews();
                showQuestion(--currentQuestionID);

            }
        });
    }


    private void setLocationOfButtonInRelativeLayout(FloatingActionButton button, String nextOrPrev) {
        RelativeLayout.LayoutParams params = null;
        switch (nextOrPrev) {
            case "next":
                params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, Double.valueOf(getHeightOfScreen() * 0.85).intValue(), 0, 0);
                button.setLayoutParams(params);
                break;
            case "previous":
                params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(Double.valueOf(getWidthOfScreen() * 0.85).intValue(), Double.valueOf(getHeightOfScreen() * 0.85).intValue(), 0, 0);
                button.setLayoutParams(params);
                break;
        }
    }

    private void sendAnswersToServer() {
        AppController appController = AppController.getController(this);
        appController.sendAnswersToServer(questionsAnswers, questionnaire.getQuestionaireID());
    }

    private static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }

    private void BuildQuestionByType(Question.Type type, int i) {
        if (type.equals(Question.Type.MULTI))
            buildMultiQuestion(i);
        else if (type.equals(Question.Type.SINGLE))
            buildSingleQuestion(i);
        else if (type.equals(Question.Type.VAS))
            buildVAS_Question(i);
        else if (type.equals(Question.Type.EQ5))
            buildEQ5Question(i);
        else
            System.out.println("please check type of Question");
    }

    private void buildEQ5Question(int i) {
        LinearLayout layout = findViewById(R.id.lin_layout);
        float sizeBestWorst = descriptionTextSize;
        float subtextSize = questionTextSize;
        String worst = this.questionnaire.getQuestions().get(i).getWorst();
        String best = this.questionnaire.getQuestions().get(i).getBest();
        String subtext = getString(R.string.betweenZeroTo100);
        SeekBar seekBar = new SeekBar(this);
        //     seekBar.setPadding(0, getHeightOfScreen() / 10, 0, 0);
        seekBar.setMax(100);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBar.setMin(0);
        }
        int width = (int) (0.9 * getWidthOfScreen());
        int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        seekBar.setThumb(getDrawable(R.drawable.custom_thumb));
        seekBar.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        seekBar.setProgress(1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                func(progress);
            }

            private void func(int progress) {
                eq5Answered = true;
                eq5result.setText(String.valueOf(progress));
                if (progress <= 33)
                    eq5face.setImageResource(R.drawable.yellowsadface);
                else if (progress < 67)
                    eq5face.setImageResource(R.drawable.yellowneutralface);
                else
                    eq5face.setImageResource(R.drawable.yellowhappyface);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //do nothing
            }
        });
        TextView bestTV = new TextView(this);
        bestTV.setText(worst + " " + best);
        bestTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,sizeBestWorst);
        bestTV.setGravity(Gravity.CENTER);
        bestTV.setPadding(0, 5, 0, 0);
        TextView subtextTV = new TextView(this);
        subtextTV.setText(subtext);
        subtextTV.setGravity(Gravity.CENTER);
        subtextTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,subtextSize);
        subtextTV.setPadding(0, 5, 0, 0);

        TextView max = new TextView(this);
        TextView min = new TextView(this);
        TextView center = new TextView(this);
        eq5result = new TextView(this);
        eq5face = new ImageView(this);
        answerEQ5SeekBar = seekBar;
        layout.addView(subtextTV);
        layout.addView(bestTV);
        LinearLayout rl = new LinearLayout(this);
        setViewOfScalaInfo(min,max,center,eq5face,rl,eq5result, layout, width, height);
        setLocationOfPlusMinusButtons(true);
    }

    private void setViewOfScalaInfo(TextView min, TextView max, TextView center, ImageView eq5face,
                                    LinearLayout rl, TextView eq5result, LinearLayout layout,
                                    int width, int height) {

        //space
        TextView space = new TextView(this);
        space.setPadding(0,100,0,0);
        space.setGravity(Gravity.CENTER);
        space.setTextSize(20);
        space.setText(" ");

        //result
        eq5result.setGravity(Gravity.CENTER);
        eq5result.setText("0");
        eq5result.setPadding(0, 20, 0, 0);
        eq5result.setTextSize(TypedValue.COMPLEX_UNIT_SP,questionTextSize);

        //emoji face
        eq5face.setImageResource(R.drawable.whitesadface);
        eq5face.setPadding(0, 5, 0, 0);
        eq5face.setLayoutParams(new LinearLayout.LayoutParams(200,200));

        //min
        min.setGravity(Gravity.START);
        min.setPadding(getWidthOfScreen()-width,0,getWidthOfScreen()-width,0);
        min.setText("0");
        min.setTextSize(20);
        min.setLayoutParams(new LinearLayout.LayoutParams(width,height, (float) 0.33));

        //max
        max.setTextSize(20);
        max.setGravity(Gravity.END);
        max.setText("100");
        max.setPadding(getWidthOfScreen()-width,0,getWidthOfScreen()-width,0);
        max.setLayoutParams(new LinearLayout.LayoutParams(width,height, (float) 0.33));

        //center
        center.setGravity(Gravity.CENTER);
        center.setText("50");
        center.setTextSize(20);
        center.setLayoutParams(new LinearLayout.LayoutParams(width,height, (float) 0.33));

        //relative layout
        rl.setOrientation(LinearLayout.HORIZONTAL);
        rl.addView(min);
        rl.addView(center);
        rl.addView(max);
        layout.addView(space);
        layout.addView(eq5face);
        layout.addView(eq5result);
        layout.addView(answerEQ5SeekBar);
        layout.addView(rl);


    }

    private void buildVAS_Question(final int i) {
        VAS_Colors = getColorsOfVAS();
        LinearLayout layout = findViewById(R.id.lin_layout);

        TextView best = new TextView(this);
        best.setText(questionnaire.getQuestions().get(i).getBest());
        setLabelsOfBestWorstConfiguration(best);
        layout.addView(best);

        int answersSize = this.questionnaire.getQuestions().get(i).getAnswers().size();
        for (Answer ans : this.questionnaire.getQuestions().get(i).getAnswers()) {
            String text = BuildTextForVasQuestion(ans.getAnswerText());
            Button ans_Button = new Button(this);
            ans_Button.setText(text);
            ans_Button.setPadding(0, 0, 0, 0);
            ans_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP,answerVasTextSize);
            final long finalAnswerID = ans.getAnswerID();
            final long finalQuestionID = currentQuestionID;
            ans_Button.setBackground(getDrawable(R.drawable.custom_button));
            boolean chosen = checkChosen(finalQuestionID,finalAnswerID, Question.Type.VAS);
            if (chosen)
                ans_Button.setBackground(getDrawable(R.drawable.custom_vas_chosen_button));
            else
                ans_Button.setBackgroundColor(GetVASAnswerColor(finalAnswerID, answersSize));
            Drawable emoji;
            if (ans.getAnswerID() < (answersSize/3))
                emoji = getDrawable(R.drawable.happyface);
            else if (ans.getAnswerID() < ((2 * answersSize )/ 3))
                emoji = getDrawable(R.drawable.neutralface);
            else
                emoji = getDrawable(R.drawable.sadface);

            Bitmap emoji_bitmap = ((BitmapDrawable) emoji).getBitmap();
            Drawable emoji_drawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(emoji_bitmap, 80, 80, true));
            emoji_drawable.setBounds(0,0,80,80);
            ans_Button.setCompoundDrawables(emoji_drawable,null,null,null);

            setButtonConfigurationForVAS(ans_Button);
            ans_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chose(finalAnswerID, finalQuestionID,answersSize );
                }

                private void chose(long chosenAnswerID, long questionID, int numberOfAnswers) {
                    System.out.println("question id: " + questionID + " , chosen answer id: " + chosenAnswerID);

                    if (!questionsAnswers.containsKey(questionID)) {
                        List<Long> tmp_list = new ArrayList<>();
                        tmp_list.add(chosenAnswerID);
                        questionsAnswers.put(questionID, tmp_list);
                        answersButtons.get(finalQuestionID).get(finalAnswerID).setBackground(getDrawable(R.drawable.custom_vas_chosen_button));
                    } else { //user has changed his answer
                        List<Long> prevAnsList = questionsAnswers.get(questionID);
                        long prevAnswer = prevAnsList.get(0);
                        if (prevAnswer != chosenAnswerID) {
                            prevAnsList.remove(0);
                            prevAnsList.add(chosenAnswerID);
                            questionsAnswers.put(questionID, prevAnsList);
                            answersButtons.get(finalQuestionID).get(prevAnswer).setBackgroundColor(GetVASAnswerColor(prevAnswer,numberOfAnswers));
                            answersButtons.get(finalQuestionID).get(finalAnswerID).setBackground(getDrawable(R.drawable.custom_vas_chosen_button));
                        }
                    }
                }
            });
            answersButtons.get(finalQuestionID).put(finalAnswerID, ans_Button);
            layout.addView(ans_Button);
        }

        TextView worstTV = new TextView(this);
        worstTV.setText(questionnaire.getQuestions().get(i).getWorst());
        setLabelsOfBestWorstConfiguration(worstTV);
        layout.addView(worstTV);


    }

    private int GetVASAnswerColor(long finalAnswerID, long numberOfAnswers) {
        long size = VAS_Colors.size();
        long index = (finalAnswerID)* (size/numberOfAnswers);
        return VAS_Colors.get(index);
    }

    private String BuildTextForVasQuestion(String answerText) {
        String language = Locale.getDefault().getDisplayLanguage();
        if (language.toUpperCase().equals("ENGLISH"))
            return answerText + "   ";
        else
            return "   " + answerText;
    }

    /**
     * @return map of answerID and color of button as integer
     */
    private Map<Long, Integer> getColorsOfVAS() {
        HashMap<Long, Integer> colors = new HashMap<>();
        long i = 0;
        colors.put(i++, ResourcesCompat.getColor(getResources(), R.color.colorVAS0, null));
        colors.put(i++, ResourcesCompat.getColor(getResources(), R.color.colorVAS1, null));
        colors.put(i++, ResourcesCompat.getColor(getResources(), R.color.colorVAS2, null));
        colors.put(i++, ResourcesCompat.getColor(getResources(), R.color.colorVAS3, null));
        colors.put(i++, ResourcesCompat.getColor(getResources(), R.color.colorVAS4, null));
        colors.put(i++, ResourcesCompat.getColor(getResources(), R.color.colorVAS5, null));
        colors.put(i++, ResourcesCompat.getColor(getResources(), R.color.colorVAS6, null));
        colors.put(i++, ResourcesCompat.getColor(getResources(), R.color.colorVAS7, null));
        colors.put(i++, ResourcesCompat.getColor(getResources(), R.color.colorVAS8, null));
        colors.put(i++, ResourcesCompat.getColor(getResources(), R.color.colorVAS9, null));
        colors.put(i, ResourcesCompat.getColor(getResources(), R.color.colorVAS10, null));
        return colors;
    }

    private void buildSingleQuestion(int i) {
        LinearLayout layout = findViewById(R.id.lin_layout);

        for (Answer ans : this.questionnaire.getQuestions().get(i).getAnswers()) {
            String text = ans.getAnswerText();
            Button ans_Button = new Button(this);
            ans_Button.setText(text);
            ans_Button.setPadding(0, 0, 0, 0);
            ans_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP,answersTextSize);
            final long finalAnswerID = ans.getAnswerID();
            final long finalQuestionID = currentQuestionID;
            boolean chosen = checkChosen(finalQuestionID,finalAnswerID, Question.Type.SINGLE);
            setButtonConfigurationForSingleAndMulti(ans_Button, chosen);
            ans_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chose(finalAnswerID, finalQuestionID);
                }

                private void chose(long chosenAnswerID, long questionID) {
                    System.out.println("question id: " + questionID + " , chosen answer id: " + chosenAnswerID);

                    if (!questionsAnswers.containsKey(questionID)) {
                        List<Long> tmp_list = new ArrayList<>();
                        tmp_list.add(chosenAnswerID);
                        questionsAnswers.put(questionID, tmp_list);
                        answersButtons.get(finalQuestionID).get(finalAnswerID).setBackground(getDrawable(R.drawable.custom_chosen_button));
                    } else { //user has changed is answer
                        List<Long> prevAnsList = questionsAnswers.get(questionID);
                        long prevAnswer = prevAnsList.get(0);
                        if (prevAnswer != chosenAnswerID) {
                            prevAnsList.remove(0);
                            prevAnsList.add(chosenAnswerID);
                            questionsAnswers.put(questionID, prevAnsList);
                            answersButtons.get(finalQuestionID).get(prevAnswer).setBackground(getDrawable(R.drawable.custom_button));
                            answersButtons.get(finalQuestionID).get(finalAnswerID).setBackground(getDrawable(R.drawable.custom_chosen_button));
                        }
                    }
                }
            });
            answersButtons.get(finalQuestionID).put(finalAnswerID, ans_Button);
            layout.addView(ans_Button);
        }
    }

    private boolean checkChosen(long finalQuestionID, long finalAnswerID, Question.Type type) {
        if (questionsAnswers.containsKey(finalQuestionID)) {
            List<Long> prevAnsList = questionsAnswers.get(finalQuestionID);
            if (type.equals(Question.Type.SINGLE) || type.equals(Question.Type.VAS)) {
                long prevAnswer = prevAnsList.get(0);
                return (prevAnswer == finalAnswerID);
            }
            else if (type.equals(Question.Type.MULTI)) {
                for (int i=0; prevAnsList!=null && i<prevAnsList.size(); i++) {
                    long prevAnswer = prevAnsList.get(i);
                    if (prevAnswer == finalAnswerID)
                        return true;
                }
            }

        }
        return  false;
    }

    private void buildMultiQuestion(final int i) {
        LinearLayout layout = findViewById(R.id.lin_layout);

        TextView multipleTV = new TextView(this);
        multipleTV.setText(R.string.multiple_choices);
        setLabelsOfBestWorstConfiguration(multipleTV);
        layout.addView(multipleTV);

        for (Answer ans : this.questionnaire.getQuestions().get(i).getAnswers()) {

            String text = ans.getAnswerText();
            Button ans_Button = new Button(this);
            if (this.questionnaire.getQuestionaireID()==0 && i==1 && ans.getAnswerID() != 0) { //medicine question on Daily Questionnaire and not first answer
                if (medicineInfo == null) {
                    medicineInfo = new HashMap<>();
                    medicineInfo.put((long) 1,getString(R.string.basicMedicine));
                    medicineInfo.put((long) 2,getString(R.string.advancedMedicine));
                    medicineInfo.put((long) 3,getString(R.string.narcoticMedicine));
                }
                String description = medicineInfo.get(ans.getAnswerID());
                CharSequence finalText = TextUtils.concat(text, "\n", description);
                ans_Button.setText(finalText);
            }
            else {
                ans_Button.setText(text);
            }
            ans_Button.setTextSize(answersTextSize);
            ans_Button.setPadding(50, 0, 50, 0);
            // ans_Button.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

            final long finalAnswerID = ans.getAnswerID();
            final long finalQuestionID = currentQuestionID;
            boolean chosen = checkChosen(finalQuestionID,finalAnswerID, Question.Type.MULTI);
            setButtonConfigurationForSingleAndMulti(ans_Button, chosen);

            ans_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chose(finalAnswerID, finalQuestionID, v);
                }

                private void chose(long chosenAnswerID, long questionID, View v) {
                    System.out.println("question id: " + questionID + " , chosen answer id: " + chosenAnswerID);
                    List<Long> alone = questionnaire.getQuestions().get(i).getAlone();
                    if (!questionsAnswers.containsKey(questionID)) { //first answer
                        List<Long> tmp_list = new ArrayList<>();
                        tmp_list.add(chosenAnswerID);
                        questionsAnswers.put(questionID, tmp_list);
                        answersButtons.get(finalQuestionID).get(finalAnswerID).setBackground(getDrawable(R.drawable.custom_chosen_button));
                    } else { //user has changed his answer or add new answer
                        List<Long> prevAnsList = questionsAnswers.get(questionID);
                        int index_cancelledAnswer = sameAnswer(prevAnsList, chosenAnswerID);
                        if (index_cancelledAnswer == -1) { //new answer
                            if (AloneIsAlreadyChosen(alone, prevAnsList)) {
                                Toast.makeText(v.getContext(), R.string.choose_else, Toast.LENGTH_SHORT).show();
                                return;
                            } else if (alone.contains(chosenAnswerID)) { //this answer should be chosen only alone
                                //remove all the rest
                                for (long prev : prevAnsList) {
                                    answersButtons.get(finalQuestionID).get(prev).setBackground(getDrawable(R.drawable.custom_button));
                                }
                                prevAnsList.clear();
                                Toast.makeText(v.getContext(), R.string.this_alone, Toast.LENGTH_SHORT).show();

                            }
                            prevAnsList.add(chosenAnswerID);
                            questionsAnswers.put(questionID, prevAnsList);
                            answersButtons.get(finalQuestionID).get(chosenAnswerID).setBackground(getDrawable(R.drawable.custom_chosen_button));
                        } else { //want to cancel exists answer
                            long prevAnswer = prevAnsList.get(index_cancelledAnswer);
                            prevAnsList.remove((index_cancelledAnswer));
                            answersButtons.get(finalQuestionID).get(prevAnswer).setBackground(getDrawable(R.drawable.custom_button));
                            if (prevAnsList.size() != 0) //there is still choices
                                questionsAnswers.put(questionID, prevAnsList);
                            else //empty list
                                questionsAnswers.remove(questionID);
                        }

                    }
                }
            });
            answersButtons.get(finalQuestionID).put(finalAnswerID, ans_Button);
            layout.addView(ans_Button);
        }
        //add info on medicines for daily 2nd question
        if (this.questionnaire.getQuestionaireID()==0 && i==1) { //medicine question on Daily Questionnaire
            Button examples = addExamplesButton();
            layout.addView(examples);
        }
    }

    private Button addExamplesButton() {
        Button examples = new Button(this);
        examples.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openExamples();
            }
        });

        examples.setText(getString(R.string.more_medicine_examples));
        examples.setTextSize(TypedValue.COMPLEX_UNIT_SP,questionTextSize);
        setButtonConfigurationForSingleAndMulti(examples, false);
        LinearLayout.LayoutParams params = new LinearLayout .LayoutParams(
                LinearLayout .LayoutParams.WRAP_CONTENT, LinearLayout .LayoutParams.WRAP_CONTENT);
        params.setMargins(10,100, 10, 10);
        examples.setBackground(getDrawable(R.drawable.custom_unclickable_button));
        examples.setLayoutParams(params);
        return examples;
    }

    private void openExamples() {
        String msg = getString(R.string.medicine_examples_basic_title) + "\n" + getString(R.string.medicine_examples_basic_info) + "\n";
        msg = msg + getString(R.string.medicine_examples_advanced_title) + "\n" + getString(R.string.medicine_examples_advanced_info) + "\n";
        msg = msg + getString(R.string.medicine_examples_narcotic_title) + "\n" + getString(R.string.medicine_examples_narcotic_info);

        new AlertDialog.Builder(QuestionnaireActivity.this)
                .setTitle(R.string.more_medicine_examples)
                .setMessage(msg)
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                })
                .setIcon(R.drawable.notif_icon)
                .show();
    }


    private boolean AloneIsAlreadyChosen(List<Long> alone, List<Long> prevAnsList) {
        for (long a : alone) {
            if (prevAnsList.contains(a))
                return true;
        }
        return false;
    }

    private int sameAnswer(List<Long> prevAnsList, long chosenAnswerID) {
        for (int i = 0; i < prevAnsList.size(); i++) {
            long l = prevAnsList.get(i);
            if (chosenAnswerID == l)
                return i;
        }
        return -1;
    }

    private void setButtonConfigurationForVAS(Button b) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        int px = getPxOfDP(125);

        params.width = px;
        params.height = (int) (px * 0.25);
        b.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        b.setLayoutParams(params);
    }

    /*
    now is implemented only by single, let's see later if we need it on multi
     */
    private void setButtonConfigurationForSingleAndMulti(Button b, boolean chosen) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 20, 10, 10);
        int width = getWidthOfScreen();
        b.setWidth(width);
        b.setGravity(Gravity.CENTER);
        b.setLayoutParams(params);
        if (chosen)
            b.setBackground(getDrawable(R.drawable.custom_chosen_button));
        else
            b.setBackground(getDrawable(R.drawable.custom_button));

    }

    private int getWidthOfScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        return width;
    }

    private int getHeightOfScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    private void setLabelsOfBestWorstConfiguration(TextView t) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        t.setGravity(Gravity.CENTER);
        t.setPadding(0, 0, 0, 0);
        t.setTextSize(15);
        t.setLayoutParams(params);
    }

    private int getPxOfDP(int i) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                i,
                getResources().getDisplayMetrics()
        );
    }

    private void openQuestionnaireActivity(String questionnaire_name, Long questionnaire_id) {
        Log.i(TAG, "questionnaire " + questionnaire_name + " has been opened");
        AppController appController = AppController.getController(this);
        Questionnaire questionnaire = appController.getQuestionnaire(questionnaire_id);
        Intent intent = new Intent(this, QuestionnaireActivity.class);
        intent.putExtra(BindingValues.REQUESTED_QUESTIONNAIRE, questionnaire);
        finish();
        startActivity(intent);
    }

    public void enlargeText(View view) {
        final float MAX_SIZE = 25;
        LinearLayout layout = findViewById(R.id.lin_layout);
        for (int i=0; i< layout.getChildCount(); i++) {
            View tmp = layout.getChildAt(i);
            if (tmp instanceof Button) {
                Button button = (Button)tmp;
                float currentSize = button.getTextSize();
                float dp = getResources().getDisplayMetrics().scaledDensity;
                float CurrentSizeInSP = currentSize/dp;
                if (CurrentSizeInSP < MAX_SIZE) {
                    CurrentSizeInSP = CurrentSizeInSP+1;
                    answersTextSize = CurrentSizeInSP;
                    button.setTextSize(TypedValue.COMPLEX_UNIT_SP,CurrentSizeInSP);
                }
            }
            else if (tmp instanceof TextView) {
                TextView textView = (TextView)tmp;
                float currentSize = textView.getTextSize();
                float dp = getResources().getDisplayMetrics().scaledDensity;
                float CurrentSizeInSP = currentSize/dp;
                if (CurrentSizeInSP < MAX_SIZE) {
                    CurrentSizeInSP = CurrentSizeInSP+1;
                    if (textView.getText().toString().contains(getString(R.string.questionnaire)))
                        questionTextSize = CurrentSizeInSP;
                    else
                        descriptionTextSize = CurrentSizeInSP;
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,CurrentSizeInSP);
                }
            }
        }
    }

    public void reduceText(View view) {
        final float MIN_SIZE = 13;
        LinearLayout layout = findViewById(R.id.lin_layout);
        for (int i=0; i< layout.getChildCount(); i++) {
            View tmp = layout.getChildAt(i);
            if (tmp instanceof Button) {
                Button button = (Button)tmp;
                float currentSize = button.getTextSize();
                float dp = getResources().getDisplayMetrics().scaledDensity;
                float CurrentSizeInSP = currentSize/dp;
                if (CurrentSizeInSP > MIN_SIZE) {
                    CurrentSizeInSP = CurrentSizeInSP-1;
                    if (isVasAnswer(button.getText().toString()))
                        answerVasTextSize = currentQuestionID;
                    else
                        answersTextSize = CurrentSizeInSP;
                    button.setTextSize(TypedValue.COMPLEX_UNIT_SP,CurrentSizeInSP);
                }
            }
            else if (tmp instanceof TextView) {
                TextView textView = (TextView)tmp;
                float currentSize = textView.getTextSize();
                float dp = getResources().getDisplayMetrics().scaledDensity;
                float CurrentSizeInSP = currentSize/dp;
                if (CurrentSizeInSP > MIN_SIZE) {
                    CurrentSizeInSP = CurrentSizeInSP-1;
                    if (textView.getText().toString().contains(getString(R.string.questionnaire)))
                        questionTextSize = CurrentSizeInSP;
                    else
                        descriptionTextSize = CurrentSizeInSP;
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,CurrentSizeInSP);
                }
            }
        }
    }

    private boolean isVasAnswer(String toString) {
        return toString.equals("1") ||
                toString.equals("2") ||
                toString.equals("3") ||
                toString.equals("4") ||
                toString.equals("5") ||
                toString.equals("6") ||
                toString.equals("7") ||
                toString.equals("8") ||
                toString.equals("9") ||
                toString.equals("10");
    }
}
