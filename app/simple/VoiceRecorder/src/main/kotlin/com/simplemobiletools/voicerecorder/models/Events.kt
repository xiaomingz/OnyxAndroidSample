package com.simplemobiletools.voicerecorder.models

class Events {
    class RecordingDuration internal constructor(val duration: Int)
    class RecordingStatus internal constructor(val status: Int) {
        companion object {
            val STATUS_STOP = 0
            val STATUS_RECORDING = 1
            val STATUS_PAUSE = 2
        }
    }
    class RecordingAmplitude internal constructor(val amplitude: Int)
    class RecordingDone internal constructor(val path: String)
    class MediaPlayingFinishEvent
    class MediaPlayingPreparedEvent
}
