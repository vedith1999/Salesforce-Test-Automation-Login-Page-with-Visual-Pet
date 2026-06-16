package com.acme.salesforce.util;

import com.acme.salesforce.config.TestConfig;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

public final class VisualAgent {
    private VisualAgent() {
    }

    public static void announce(WebDriver driver, String message) {
        if (!TestConfig.visualAgentEnabled()) {
            return;
        }

        try {
            ((JavascriptExecutor) driver).executeScript(agentScript(), message);
            pause();
        } catch (WebDriverException exception) {
            // Visual narration is helpful for demos, but it should never make tests fail.
        }
    }

    public static void highlight(WebDriver driver, WebElement element, String message) {
        if (!TestConfig.visualAgentEnabled()) {
            return;
        }

        try {
            ((JavascriptExecutor) driver).executeScript(highlightScript(), element, message);
            pause();
        } catch (WebDriverException exception) {
            // Stale or cross-page elements can happen during navigation; continue the test flow.
        }
    }

    private static void pause() {
        try {
            Thread.sleep(TestConfig.visualStepDelay().toMillis());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    private static String agentScript() {
        return """
                const message = arguments[0];
                let root = document.getElementById('codex-visual-agent');

                function setStyles(element, styles) {
                    Object.entries(styles).forEach(([key, value]) => element.style[key] = value);
                }

                if (!root) {
                    root = document.createElement('div');
                    root.id = 'codex-visual-agent';
                    setStyles(root, {
                        position: 'fixed',
                        right: '24px',
                        bottom: '24px',
                        zIndex: '2147483647',
                        pointerEvents: 'none',
                        fontFamily: 'Avenir Next, Verdana, sans-serif'
                    });

                    const card = document.createElement('div');
                    card.className = 'codex-agent-card';
                    setStyles(card, {
                        display: 'flex',
                        alignItems: 'center',
                        gap: '14px',
                        minWidth: '330px',
                        maxWidth: '420px',
                        padding: '14px 16px',
                        borderRadius: '24px',
                        background: 'linear-gradient(135deg, rgba(12, 31, 68, 0.94), rgba(0, 94, 125, 0.92))',
                        color: '#ffffff',
                        boxShadow: '0 18px 45px rgba(0, 0, 0, 0.32)',
                        border: '1px solid rgba(255, 255, 255, 0.28)'
                    });

                    const pet = document.createElement('div');
                    pet.className = 'codex-pet';
                    setStyles(pet, {
                        position: 'relative',
                        width: '54px',
                        height: '54px',
                        flex: '0 0 54px',
                        borderRadius: '42% 42% 48% 48%',
                        background: 'linear-gradient(145deg, #f7c873, #f08b4f)',
                        boxShadow: 'inset -8px -10px 0 rgba(120, 60, 30, 0.12), 0 10px 20px rgba(0, 0, 0, 0.25)'
                    });

                    const leftEar = document.createElement('div');
                    setStyles(leftEar, {
                        position: 'absolute',
                        width: '18px',
                        height: '18px',
                        left: '6px',
                        top: '-6px',
                        borderRadius: '6px 14px 4px 14px',
                        background: '#f7c873',
                        transform: 'rotate(-24deg)'
                    });

                    const rightEar = document.createElement('div');
                    setStyles(rightEar, {
                        position: 'absolute',
                        width: '18px',
                        height: '18px',
                        right: '6px',
                        top: '-6px',
                        borderRadius: '14px 6px 14px 4px',
                        background: '#f7c873',
                        transform: 'rotate(24deg)'
                    });

                    const leftEye = document.createElement('div');
                    setStyles(leftEye, {
                        position: 'absolute',
                        width: '7px',
                        height: '9px',
                        left: '16px',
                        top: '22px',
                        borderRadius: '50%',
                        background: '#1f2937'
                    });

                    const rightEye = document.createElement('div');
                    setStyles(rightEye, {
                        position: 'absolute',
                        width: '7px',
                        height: '9px',
                        right: '16px',
                        top: '22px',
                        borderRadius: '50%',
                        background: '#1f2937'
                    });

                    const nose = document.createElement('div');
                    setStyles(nose, {
                        position: 'absolute',
                        width: '8px',
                        height: '6px',
                        left: '23px',
                        top: '34px',
                        borderRadius: '50%',
                        background: '#8a3d2f'
                    });

                    const content = document.createElement('div');
                    setStyles(content, {
                        display: 'grid',
                        gap: '4px',
                        lineHeight: '1.28'
                    });

                    const label = document.createElement('div');
                    label.textContent = 'Salesforce Security Agent';
                    setStyles(label, {
                        fontSize: '12px',
                        letterSpacing: '0.11em',
                        textTransform: 'uppercase',
                        opacity: '0.74',
                        fontWeight: '700'
                    });

                    const bubble = document.createElement('div');
                    bubble.className = 'codex-agent-message';
                    setStyles(bubble, {
                        fontSize: '15px',
                        fontWeight: '700'
                    });

                    pet.append(leftEar, rightEar, leftEye, rightEye, nose);
                    content.append(label, bubble);
                    card.append(pet, content);
                    root.append(card);
                    document.documentElement.append(root);
                }

                const bubble = root.querySelector('.codex-agent-message');
                const pet = root.querySelector('.codex-pet');
                if (bubble) {
                    bubble.textContent = message;
                }
                if (pet) {
                    pet.animate([
                        { transform: 'translateY(0) rotate(0deg)' },
                        { transform: 'translateY(-12px) rotate(-4deg)' },
                        { transform: 'translateY(0) rotate(3deg)' },
                        { transform: 'translateY(-5px) rotate(0deg)' },
                        { transform: 'translateY(0) rotate(0deg)' }
                    ], { duration: 850, iterations: 1, easing: 'cubic-bezier(.2,.8,.2,1)' });
                }
                """;
    }

    private static String highlightScript() {
        return """
                const element = arguments[0];
                const message = arguments[1];
                element.scrollIntoView({ block: 'center', inline: 'center' });

                const originalOutline = element.style.outline;
                const originalBoxShadow = element.style.boxShadow;
                const originalTransition = element.style.transition;

                element.style.transition = 'outline 150ms ease, box-shadow 150ms ease';
                element.style.outline = '4px solid #ffd166';
                element.style.boxShadow = '0 0 0 8px rgba(255, 209, 102, 0.34)';

                setTimeout(() => {
                    element.style.outline = originalOutline;
                    element.style.boxShadow = originalBoxShadow;
                    element.style.transition = originalTransition;
                }, 1400);

                const event = new CustomEvent('codex-agent-message', { detail: message });
                window.dispatchEvent(event);

                let root = document.getElementById('codex-visual-agent');
                if (root) {
                    const bubble = root.querySelector('.codex-agent-message');
                    const pet = root.querySelector('.codex-pet');
                    if (bubble) {
                        bubble.textContent = message;
                    }
                    if (pet) {
                        pet.animate([
                            { transform: 'scale(1)' },
                            { transform: 'scale(1.12)' },
                            { transform: 'scale(1)' }
                        ], { duration: 650, iterations: 1, easing: 'ease-out' });
                    }
                }
                """;
    }
}
