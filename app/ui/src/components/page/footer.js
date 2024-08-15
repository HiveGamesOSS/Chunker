import React, {Component} from "react";

export class Footer extends Component {
    render() {
        return (<footer id="footer">
                <nav className="footer_top">
                    <div className="wrapper">
                        <ul>
                            <li><a href="https://github.com/HiveGamesOSS/Chunker" target="_blank"
                                   rel="noopener noreferrer">GitHub</a></li>
                            <li><a href="https://github.com/HiveGamesOSS/Chunker/network/dependencies" target="_blank"
                                   rel="noopener noreferrer">Attributions</a></li>
                        </ul>
                    </div>
                </nav>
                <div className="wrapper">
                    <div className="footer_bottom">
                        <a href="https://hivegames.io" target="_blank" rel="noopener noreferrer"><img
                            src="images/hive.png"
                            alt="Hive Logo"/></a>
                        <p>Proudly made by Hive Games under exclusive license to Minecraft<br/><span
                            className="copy">&copy; Hive Games Limited {new Date().getFullYear()}</span>
                        </p>
                        <span className="build">
                        {(window.chunker && window.chunker.version) || "unknown"}-{(window.chunker && window.chunker.gitVersion) || "unknown"}
                    </span>
                    </div>
                </div>
            </footer>
        );
    }
}
