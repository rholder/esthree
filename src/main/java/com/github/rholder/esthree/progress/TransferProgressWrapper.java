/*
 * Copyright 2014 Ray Holder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.rholder.esthree.progress;

import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.services.s3.transfer.internal.TransferProgressImpl;

/**
 * Wrap Amazon's {@link TransferProgressImpl} and adapt it to a {@link Progress}.
 */
public class TransferProgressWrapper implements Progress {

    public TransferProgressImpl transferProgress;

    public TransferProgressWrapper(TransferProgressImpl transferProgress) {
        this.transferProgress = transferProgress;
    }

    public TransferProgressWrapper(TransferProgress transferProgress) {
        // if it's not this implementation then just 'esplode here
        this.transferProgress = (TransferProgressImpl) transferProgress;
    }

    @Override
    public double getPercentTransferred() {
        return transferProgress.getPercentTransferred();
    }

    @Override
    public long getBytesTransferred() {
        return transferProgress.getBytesTransferred();
    }

    @Override
    public void setTotalBytesToTransfer(long totalBytesToTransfer) {
        transferProgress.setTotalBytesToTransfer(totalBytesToTransfer);
    }

    @Override
    public long getTotalBytesToTransfer() {
        return transferProgress.getTotalBytesToTransfer();
    }

    @Override
    public void updateProgress(long bytes) {
        transferProgress.updateProgress(bytes);
    }
}
