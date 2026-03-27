import hljs from 'highlight.js';
import installLineNumbers from './highlightjs-line-numbers.js';
import 'highlight.js/styles/agate.css';

installLineNumbers(hljs, window, document);

hljs.highlightAll();
// hljs.initLineNumbersOnLoad(); // Disabled - line numbers removed for cleaner code blocks
