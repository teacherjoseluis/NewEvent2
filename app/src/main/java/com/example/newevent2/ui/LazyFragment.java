package com.example.newevent2.ui;

import androidx.annotation.CallSuper;
import androidx.fragment.app.Fragment;

public class LazyFragment extends Fragment {

        private boolean _wasVisible = false;

        @CallSuper
        protected void onFirstVisible(){
            _wasVisible = true;
        }

        protected void onVisibilityChange(boolean isVisible) {
            throw new UnsupportedOperationException("method not overridden");
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            // is full initialized
            if (LazyFragment.this.getActivity() == null) return;

            // is first show - lazy load data
            if (!_wasVisible) {
                onFirstVisible();
            }

            // call visibility change
            try {
                onVisibilityChange(isVisibleToUser);
            } catch (UnsupportedOperationException e){
                // child not override 'onVisibilityChange' method
            }
        }

    }
