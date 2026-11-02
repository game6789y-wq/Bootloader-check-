use jni::objects::{JClass, JString};
use jni::sys::jstring;
use jni::JNIEnv;

fn evaluate(flash_locked: &str, boot_locked: &str, verified: &str, vbmeta: &str) -> &'static str {
    let verified = verified.trim().to_ascii_lowercase();
    let vbmeta = vbmeta.trim().to_ascii_lowercase();

    // Explicit negative signals take priority over positive/locked signals.
    if verified == "orange"
        || flash_locked.trim() == "0"
        || boot_locked.trim() == "0"
        || vbmeta == "unlocked"
    {
        return "UNLOCKED";
    }
    if verified == "yellow" {
        return "TAMPERED";
    }

    let locked_signal = verified == "green"
        || flash_locked.trim() == "1"
        || boot_locked.trim() == "1"
        || vbmeta == "locked";

    if locked_signal {
        "LOCKED"
    } else {
        "UNKNOWN"
    }
}

#[no_mangle]
pub extern "system" fn Java_com_example_data_detector_NativeBootloaderBridge_nativeEvaluateLockStatus(
    mut env: JNIEnv,
    _class: JClass,
    flash_locked: JString,
    boot_locked: JString,
    verified: JString,
    vbmeta: JString,
) -> jstring {
    let flash: String = env.get_string(&flash_locked).map(|s| s.to_string_lossy().into_owned()).unwrap_or_default();
    let boot: String = env.get_string(&boot_locked).map(|s| s.to_string_lossy().into_owned()).unwrap_or_default();
    let state: String = env.get_string(&verified).map(|s| s.to_string_lossy().into_owned()).unwrap_or_default();
    let meta: String = env.get_string(&vbmeta).map(|s| s.to_string_lossy().into_owned()).unwrap_or_default();
    let result = evaluate(&flash, &boot, &state, &meta);
    env.new_string(result).map(|s| s.into_raw()).unwrap_or(std::ptr::null_mut())
}

#[cfg(test)]
mod tests {
    use super::evaluate;

    #[test]
    fn reports_unlocked_from_orange_state() {
        assert_eq!(evaluate("", "", "orange", ""), "UNLOCKED");
    }

    #[test]
    fn reports_locked_from_explicit_locked_signal() {
        assert_eq!(evaluate("1", "", "", ""), "LOCKED");
    }

    #[test]
    fn missing_signals_remain_unknown() {
        assert_eq!(evaluate("", "", "", ""), "UNKNOWN");
    }

    #[test]
    fn custom_key_is_tampered() {
        assert_eq!(evaluate("", "", "yellow", ""), "TAMPERED");
    }
}
