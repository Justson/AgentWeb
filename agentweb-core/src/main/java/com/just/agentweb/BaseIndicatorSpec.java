package com.just.agentweb;

/**
 * source code  https://github.com/Justson/AgentWeb
 */

public interface BaseIndicatorSpec {
    /**
     * indicator
     */
    void show();

    void hide();

    void reset();

    void setProgress(int newProgress);

}
