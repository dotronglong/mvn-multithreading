package com.dotronglong.multithreading.plugin;
/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2016 Do Trong Long
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import com.dotronglong.multithreading.app.AppAware;
import org.w3c.dom.Element;

/**
 * PluginAware
 *
 * {Purpose of This Class}
 *
 * @author Long Do
 * @version 1.0.0
 * @since Jul 21, 2016
 */
public interface PluginAware {
    /* Name of plugin */
    String getName();

    void run();

    void setApp(AppAware app);

    AppAware getApp();

    /* Set appropriate XML document <plugin> */
    void setPluginElement(Element element);

    Element getPluginElement();
}
