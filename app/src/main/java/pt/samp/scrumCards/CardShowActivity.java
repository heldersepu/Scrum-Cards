package pt.samp.scrumCards;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class CardShowActivity extends Activity {
    public static final String CARD_TO_SHOW = "pt.samp.scrumCards.CardPosToShow";
    private static final int FLING_MINIMUM_INTERVAL = 50;
    private static Globals g;

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        g = (Globals)getApplication();
        Preferences.setWindowFlags(this, true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show);

        int cardPosition = getIntent().getIntExtra(CARD_TO_SHOW, 0);
        LinearLayout view = (LinearLayout) findViewById(R.id.show_container);
        View cardView = Card.createCardView(this, view, cardPosition);
        gestureDetector = new GestureDetector(this, new MyGestureDetector(this));
        view.addView(cardView);
        if (Preferences.keepHidden() && !g.isCardVisible) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector != null && gestureDetector.onTouchEvent(event);
    }

    private class MyGestureDetector extends SimpleOnGestureListener {
        private final Activity activity;

        public MyGestureDetector(Activity activity) {
            this.activity = activity;
        }

        private View getContainerView() {
            return activity.findViewById(R.id.show_container);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float difX = Math.abs(e2.getRawX() - e1.getRawX());
            float difY = Math.abs(e2.getRawY() - e1.getRawY());

            if (difX > FLING_MINIMUM_INTERVAL || difY > FLING_MINIMUM_INTERVAL) {
                activity.finish();
            } else {
                toggleCardVisibility(getContainerView());
            }
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            toggleCardVisibility(getContainerView());
            return true;
        }
    }

    private void toggleCardVisibility(View view) {
        if (Preferences.tapToHideShow()) {
            view.setVisibility(view.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
            g.isCardVisible = view.getVisibility() == View.VISIBLE;
        }
    }
}
